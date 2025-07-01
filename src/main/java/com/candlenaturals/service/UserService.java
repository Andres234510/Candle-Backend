package com.candlenaturals.service;

import com.candlenaturals.entity.User;
import com.candlenaturals.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication; // Importa Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails; // Importa UserDetails
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Recupera el usuario actualmente autenticado.
     * Utiliza el contexto de seguridad de Spring para obtener el email del usuario.
     * @return El objeto User del usuario autenticado.
     * @throws ResponseStatusException si el usuario no está autenticado o no se encuentra.
     */
    public User getCurrentAuthenticatedUser() {
        // Obtener el objeto de autenticación del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        // El 'principal' puede ser un String (email) o un UserDetails (si configuraste un UserDetailsService)
        String userEmail;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo obtener el email del usuario autenticado");
        }

        // Buscar el usuario en la base de datos por su email
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado en la base de datos"));
    }

    // Puedes añadir otros métodos si necesitas actualizar el perfil, etc.
    public User updateUserProfile(Long userId, User updatedUser) {
        // Implementa la lógica para actualizar el perfil del usuario
        // Asegúrate de que el usuario que intenta actualizar sea el mismo que el usuario autenticado
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Aquí solo actualizamos los campos permitidos
        existingUser.setNombre(updatedUser.getNombre());
        existingUser.setApellido(updatedUser.getApellido());
        existingUser.setTelefono(updatedUser.getTelefono());
        // No actualices el email o el rol directamente desde aquí sin validación adicional o un endpoint específico.
        // Si la contraseña se va a actualizar, debe hacerse a través de un endpoint de cambio de contraseña que use PasswordEncoder.

        return userRepository.save(existingUser);
    }
}