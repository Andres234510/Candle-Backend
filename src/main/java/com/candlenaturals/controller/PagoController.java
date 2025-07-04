package com.candlenaturals.controller;

import com.candlenaturals.service.MercadoPagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final MercadoPagoService mercadoPagoService;

    public PagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/crear-preferencia")
    public Map<String, String> crearPreferencia(@RequestBody Map<String, Object> datos) {
        // Asumiendo que 'datos' ahora contiene una clave "items" que es una lista de mapas
        List<Map<String, Object>> items = (List<Map<String, Object>>) datos.get("items");

        // Pasar los items directamente al servicio de Mercado Pago
        String preferenceId = mercadoPagoService.crearPreferencia(items); // Ajustar el método en MercadoPagoService
        return Map.of("id", preferenceId);
    }
}
