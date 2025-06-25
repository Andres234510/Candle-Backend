package com.candlenaturals.controller;

import com.candlenaturals.service.MercadoPagoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private final MercadoPagoService mercadoPagoService;

    public PagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/crear-preferencia")
    public Map<String, String> crearPreferencia(@RequestBody Map<String, Object> datos) {
        String titulo = (String) datos.get("titulo");
        int cantidad = (Integer) datos.get("cantidad");
        float precio = Float.parseFloat(datos.get("precio").toString());

        String preferenceId = mercadoPagoService.crearPreferencia(titulo, cantidad, precio);
        return Map.of("id", preferenceId);
    }
}
