package com.candlenaturals.dto;

import com.candlenaturals.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private Role rol; // "cliente" o "administrador"
}
