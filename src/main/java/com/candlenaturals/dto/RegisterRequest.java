package com.candlenaturals.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private String rol; // "cliente" o "administrador"
}
