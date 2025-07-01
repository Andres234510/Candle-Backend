package com.candlenaturals.controller;

import com.candlenaturals.dto.AuthResponse;
import com.candlenaturals.dto.LoginRequest;
import com.candlenaturals.dto.RegisterRequest;
import com.candlenaturals.entity.User;
import com.candlenaturals.repository.UserRepository;
import com.candlenaturals.security.JwtService;
import com.candlenaturals.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder; // Importar para limpiar el contexto

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Controlador para login, registro y autenticación con Google")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y devuelve un token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Autenticar con Google", description = "Autentica a un usuario usando un token de Google")
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        if (token == null) {
            return ResponseEntity.badRequest().body("Token de Google no proporcionado");
        }
        try {
            AuthResponse response = authService.loginWithGoogle(token);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al autenticar con Google");
        }
    }

    @Operation(summary = "Refrescar token", description = "Devuelve un nuevo token JWT si el actual es válido")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }

        String token = authHeader.substring(7);

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }

        String newToken = jwtService.generateToken(user);

        // También podrías incluir el rol en el refresh token response si es necesario para el frontend
        return ResponseEntity.ok(AuthResponse.builder().token(newToken).rol(user.getRol()).build());
    }

    // ******* NUEVO ENDPOINT PARA LOGOUT *******
    @Operation(summary = "Cerrar sesión", description = "Invalida la sesión del usuario (limpia el contexto de seguridad).")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // En un sistema basado en JWT, la "invalidación" real del token ocurre cuando expira.
        // Sin embargo, podemos limpiar el SecurityContext del servidor para la solicitud actual.
        // Si necesitas una invalidación real (antes de la expiración), deberías implementar
        // una lista negra de tokens JWT en el JwtService y consultarla en JwtAuthenticationFilter.

        // Limpiar el contexto de seguridad para la solicitud actual
        SecurityContextHolder.clearContext();

        // Opcional: Aquí podrías añadir el token a una lista negra si tuvieras una.
        // String token = extractTokenFromRequest(request);
        // if (token != null) {
        //     jwtService.blacklistToken(token); // Necesitarías implementar este método
        // }

        return ResponseEntity.ok("Sesión cerrada exitosamente.");
    }

    // Método auxiliar para extraer el token (solo si lo necesitas para una lista negra aquí)
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}