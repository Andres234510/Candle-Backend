package com.candlenaturals.security;

import com.candlenaturals.entity.User;
import com.candlenaturals.repository.UserRepository; // Necesario para cargar el usuario
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository; // Mantén la inyección de UserRepository

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // OMITIR si no es un bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extrae el token
        userEmail = jwtService.extractUsername(jwt); // Extrae el email del token

        // Si el email existe en el token Y no hay ya una autenticación en el contexto de seguridad
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargar el UserDetails desde tu UserRepository (o un UserDetailsService si lo tuvieras)
            User userDetails = userRepository.findByEmail(userEmail)
                    .orElse(null); // Si no se encuentra el usuario, es null

            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {
                // Crear el token de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // El principal DEBE ser un UserDetails object (tu clase User lo implementa)
                        null, // Las credenciales son null para tokens JWT ya validados
                        userDetails.getAuthorities() // Asigna las autoridades (roles) del usuario
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Establecer el token de autenticación en el SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // Este filtro NO DEBE ejecutarse para las rutas de autenticación públicas
        return path.startsWith("/api/auth");
    }
}