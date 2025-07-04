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

    public String crearPreferencia(List<Map<String, Object>> items) {
        RestTemplate restTemplate = new RestTemplate();

        // El cuerpo ya recibe la lista de ítems directamente
        Map<String, Object> body = Map.of(
                "items", items, // Usar la lista de ítems recibida
                "back_urls", Map.of(
                        "success", "http://localhost:5173/pago-exitoso",
                        "failure", "http://localhost:5173/pago-fallido",
                        "pending", "http://localhost:5173/pago-pendiente"
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
