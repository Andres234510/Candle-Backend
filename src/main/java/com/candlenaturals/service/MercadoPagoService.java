package com.candlenaturals.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoService {

    private static final String ACCESS_TOKEN = "TEST-2771343550066865-062422-34b3e39958cea0195147edf969f86380-2513633161";
    private static final String API_URL = "https://api.mercadopago.com/checkout/preferences";

    public String crearPreferencia(String titulo, int cantidad, float precio) {
        RestTemplate restTemplate = new RestTemplate();

        // Cuerpo de la preferencia
        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "title", titulo,
                        "quantity", cantidad,
                        "unit_price", precio,
                        "currency_id", "COP"
                )),
                "back_urls", Map.of(
                        "success", "http://localhost:3000/pago-exitoso",
                        "failure", "http://localhost:3000/pago-fallido",
                        "pending", "http://localhost:3000/pago-pendiente"
                ),
                "auto_return", "approved"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody().get("id").toString(); // devuelve el preferenceId
        } else {
            throw new RuntimeException("Error al crear preferencia de Mercado Pago");
        }
    }
}
