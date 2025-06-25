package com.candlenaturals.dto;

import com.candlenaturals.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private Role rol;
    private String message;
}
