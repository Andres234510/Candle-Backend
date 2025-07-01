package com.candlenaturals.controller;

import com.candlenaturals.entity.User;
import com.candlenaturals.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize; // Importa PreAuthorize

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Obtener perfil del usuario autenticado", description = "Devuelve los detalles del usuario que está actualmente autenticado.")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('cliente', 'administrador')") // Permite a clientes y administradores acceder
    public ResponseEntity<User> getAuthenticatedUserProfile() {
        User user = userService.getCurrentAuthenticatedUser();
        // Es buena práctica no devolver la contraseña en la respuesta
        user.setPassword(null); // Establece la contraseña a null antes de enviarla
        return ResponseEntity.ok(user);
    }

    /*
    // Ejemplo de un endpoint para actualizar el perfil
    @Operation(summary = "Actualizar perfil del usuario", description = "Actualiza los detalles del usuario autenticado.")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('cliente', 'administrador')")
    public ResponseEntity<User> updateAuthenticatedUserProfile(@RequestBody User userUpdates) {
        // Asegúrate de que userUpdates solo contenga los campos que se pueden actualizar (nombre, apellido, telefono, etc.)
        // No permitas que el cliente cambie su ID, email, rol o contraseña directamente a través de este endpoint
        User currentUser = userService.getCurrentAuthenticatedUser(); // Obtener el usuario actual
        User updatedUser = userService.updateUserProfile(currentUser.getId_usuario(), userUpdates);
        updatedUser.setPassword(null); // Limpia la contraseña antes de enviar
        return ResponseEntity.ok(updatedUser);
    }
    */
}