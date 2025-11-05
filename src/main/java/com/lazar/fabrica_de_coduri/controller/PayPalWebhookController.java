
package com.lazar.fabrica_de_coduri.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lazar.fabrica_de_coduri.service.PayPalWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/paypal")
public class PayPalWebhookController {

    private final PayPalWebhookService service;
    private final ObjectMapper om = new ObjectMapper();

    public PayPalWebhookController(PayPalWebhookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> handle(@RequestHeader("PayPal-Transmission-Id") String transmissionId,
                                         @RequestHeader("PayPal-Transmission-Time") String transmissionTime,
                                         @RequestHeader("PayPal-Cert-Url") String certUrl,
                                         @RequestHeader("PayPal-Auth-Algo") String authAlgo,
                                         @RequestHeader("PayPal-Transmission-Sig") String transmissionSig,
                                         @RequestBody String rawBody) {
        try {
            Map<String, Object> event = om.readValue(rawBody, Map.class);
            service.handle(transmissionId, transmissionTime, certUrl, authAlgo, transmissionSig, rawBody, event);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            // return 200 to avoid retries in dev, dar în prod poți trimite 400 pentru semnături invalide
            return ResponseEntity.ok("IGNORED: " + e.getMessage());
        }
    }
}
