package com.candlenaturals.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails

import java.util.Collection; // Importar Collection
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // Implementar UserDetails
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String email;

    private String password;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private Role rol;

    private Boolean activo = true; // Por defecto true para usuarios activos

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // El email se usa como nombre de usuario para Spring Security
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Asume que la cuenta nunca expira para este caso
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Asume que la cuenta nunca se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Asume que las credenciales (contraseña) nunca expiran
    }

    @Override
    public boolean isEnabled() {
        return this.activo; // Utiliza el campo 'activo' para determinar si el usuario está habilitado
    }
}