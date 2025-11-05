package com.lazar.fabrica_de_coduri.paypal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayPalClient {
    private final RestTemplate rest = new RestTemplate();

    private final String base;
    private final String clientId;
    private final String clientSecret;

    public PayPalClient(@Value("${paypal.mode}") String mode,
                        @Value("${paypal.client-id}") String clientId,
                        @Value("${paypal.client-secret}") String clientSecret) {
        this.base = "live".equalsIgnoreCase(mode) ?
                "https://api-m.paypal.com" : "https://api-m.sandbox.paypal.com";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String accessToken() {
        String url = base + "/v1/oauth2/token";
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String basic = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        h.set("Authorization", "Basic " + basic);

        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");

        ResponseEntity<Map> resp = rest.postForEntity(url, new HttpEntity<>(form, h), Map.class);
        if (!resp.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("PayPal token failed: " + resp.getStatusCode());
        return String.valueOf(resp.getBody().get("access_token"));
    }

    public Map createOrder(String accessToken, Map payload) {
        String url = base + "/v2/checkout/orders";
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForObject(url, new HttpEntity<>(payload, h), Map.class);
    }

    public Map captureOrder(String accessToken, String orderId) {
        String url = base + "/v2/checkout/orders/" + orderId + "/capture";
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForObject(url, new HttpEntity<>("{}", h), Map.class);
    }

    public Map getOrder(String accessToken, String orderId) {
        String url = base + "/v2/checkout/orders/" + orderId;
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(h), Map.class).getBody();
    }

    public boolean verifyWebhookSignature(String transmissionId,
                                          String transmissionTime,
                                          String certUrl,
                                          String authAlgo,
                                          String transmissionSig,
                                          String webhookId,
                                          Object webhookEventBody) {
        String url = base + "/v1/notifications/verify-webhook-signature";
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken());
        h.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("transmission_id", transmissionId);
        payload.put("transmission_time", transmissionTime);
        payload.put("cert_url", certUrl);
        payload.put("auth_algo", authAlgo);
        payload.put("transmission_sig", transmissionSig);
        payload.put("webhook_id", webhookId);
        payload.put("webhook_event", webhookEventBody);

        Map resp = rest.postForObject(url, new HttpEntity<>(payload, h), Map.class);
        return resp != null && "SUCCESS".equalsIgnoreCase(String.valueOf(resp.get("verification_status")));
    }
}
