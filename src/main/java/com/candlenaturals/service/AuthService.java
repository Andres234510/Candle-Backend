package com.candlenaturals.service;

import com.candlenaturals.dto.LoginRequest;
import com.candlenaturals.dto.RegisterRequest;
import com.candlenaturals.dto.AuthResponse;
import com.candlenaturals.entity.Role;
import com.candlenaturals.entity.User;
import com.candlenaturals.repository.UserRepository;
import com.candlenaturals.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        // Buscar el usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Comparar manualmente las contraseñas
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }



    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(Role.valueOf(request.getRol()))

                .activo(true)
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
