package com.lazar.fabrica_de_coduri.paypal;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.model.CourseOwnership;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.CourseOwnershipRepository;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PayPalService {

    private final PayPalClient client;
    private final CourseRepository courseRepo;
    private final CourseOwnershipRepository ownershipRepo;
    private final UserRepository userRepo;

    private final String currency;
    private final BigDecimal ronToEur;

    public PayPalService(PayPalClient client,
                         CourseRepository courseRepo,
                         CourseOwnershipRepository ownershipRepo,
                         UserRepository userRepo,
                         @Value("${paypal.currency}") String currency,
                         @Value("${paypal.ron-to-eur-rate}") BigDecimal ronToEur) {
        this.client = client;
        this.courseRepo = courseRepo;
        this.ownershipRepo = ownershipRepo;
        this.userRepo = userRepo;
        this.currency = currency;
        this.ronToEur = ronToEur;
    }

    public Map<String, Object> createOrder(String slug, String principalName) {
        Course course = courseRepo.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curs inexistent"));

        User user = userRepo.findByEmail(principalName)
                .orElseGet(() -> userRepo.findByUsername(principalName)
                        .orElseThrow(() -> new RuntimeException("User inexistent")));

        // dacă deja deține cursul, nu crea comanda
        boolean owns = ownershipRepo.existsByUserIdAndCourseIdAndStatus(user.getId(), course.getId(), CourseOwnership.Status.PAID);
        if (owns) throw new RuntimeException("Deții deja cursul.");

        // conversie RON -> EUR (sau lasă dacă ai deja EUR în DB)
        BigDecimal priceRon = course.getPrice();
        BigDecimal priceEur = priceRon.multiply(ronToEur).setScale(2, RoundingMode.HALF_UP);

        // construiește payload
        Map<String, Object> amount = Map.of(
                "currency_code", currency,
                "value", priceEur.toPlainString()
        );

        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("reference_id", course.getSlug());
        purchaseUnit.put("description", course.getTitle());
        purchaseUnit.put("amount", amount);
        purchaseUnit.put("custom_id", String.valueOf(user.getId()));

        Map<String, Object> appContext = Map.of(
                "shipping_preference", "NO_SHIPPING",
                "brand_name", "Fabrica de Coduri",
                "user_action", "PAY_NOW"
        );

        Map<String, Object> body = new HashMap<>();
        body.put("intent", "CAPTURE");
        body.put("purchase_units", List.of(purchaseUnit));
        body.put("application_context", appContext);

        String token = client.accessToken();
        Map resp = client.createOrder(token, body);
        return resp;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> captureOrder(String orderId, String principalName) {
        String token = client.accessToken();
        Map resp = client.captureOrder(token, orderId);

        // status OK?
        String status = String.valueOf(resp.get("status"));
        if (!"COMPLETED".equalsIgnoreCase(status) && !"APPROVED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Captura a eșuat: " + status);
        }

        // extrage info unit -> reference_id = slug
        List<Map<String, Object>> purchaseUnits = (List<Map<String, Object>>) resp.get("purchase_units");
        if (purchaseUnits == null || purchaseUnits.isEmpty())
            throw new RuntimeException("Răspuns PayPal invalid (fără purchase_units)");

        String slug = String.valueOf(purchaseUnits.get(0).get("reference_id"));
        Course course = courseRepo.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Curs inexistent"));

        User user = userRepo.findByEmail(principalName)
                .orElseGet(() -> userRepo.findByUsername(principalName)
                        .orElseThrow(() -> new RuntimeException("User inexistent")));

        // marchează ownership (idempotent)
        ownershipRepo.findByUserIdAndCourseId(user.getId(), course.getId()).ifPresentOrElse(co -> {
            co.setStatus(CourseOwnership.Status.PAID);
            co.setPricePaid(course.getPrice());
            ownershipRepo.save(co);
        }, () -> {
            CourseOwnership co = new CourseOwnership();
            co.setUser(user);
            co.setCourse(course);
            co.setStatus(CourseOwnership.Status.PAID);
            co.setPricePaid(course.getPrice());
            ownershipRepo.save(co);
        });

        return resp;
    }
}
