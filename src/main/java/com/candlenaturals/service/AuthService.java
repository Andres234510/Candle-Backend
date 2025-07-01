package com.candlenaturals.service;

import com.candlenaturals.dto.LoginRequest;
import com.candlenaturals.dto.RegisterRequest;
import com.candlenaturals.dto.AuthResponse;
import com.candlenaturals.entity.Role;
import com.candlenaturals.entity.User;
import com.candlenaturals.repository.UserRepository;
import com.candlenaturals.security.JwtService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    @Getter
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

        // ¡AQUÍ ESTÁ EL CAMBIO! Incluye el rol del usuario en la respuesta.
        return AuthResponse.builder()
                .token(token)
                .rol(user.getRol()) // <-- Añade esta línea
                .build();
    }


    public AuthResponse register(RegisterRequest request) {
        try {
            // Convierte el rol (string) a minúsculas
            Role roleEnum = Role.valueOf(request.getRol().toString().toLowerCase());

            User user = User.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .telefono(request.getTelefono())
                    .rol(roleEnum)
                    .activo(true)
                    .build();

            userRepository.save(user);

            emailService.sendConfirmationEmail(user.getEmail(), user.getNombre());

            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .build();

        } catch (IllegalArgumentException e) {
            // Error si el rol no existe en el enum
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido: " + request.getRol());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar usuario: " + e.getMessage());
        }
    }


    public AuthResponse loginWithGoogle(String idToken) {
        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = firebaseToken.getEmail();
            String name = firebaseToken.getName(); // Nombre completo
            String uid = firebaseToken.getUid();

            if (email == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo obtener el email del token");
            }

            // Buscar si el usuario ya existe en tu base de datos
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // Si no existe, lo creamos con rol por defecto (cliente)
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setNombre(name != null ? name.split(" ")[0] : "Usuario");
                        newUser.setApellido(name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "");
                        newUser.setRol(Role.cliente);
                        newUser.setActivo(true);
                        newUser.setPassword(passwordEncoder.encode(uid)); // NO se usa, pero se requiere por el modelo
                        return userRepository.save(newUser);
                    });

            // Generar token JWT propio
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de Google inválido o expirado");
        }
    }

}