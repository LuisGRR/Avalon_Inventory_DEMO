package com.avalon.Avalon_Inventory.infrastructure.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.avalon.Avalon_Inventory.infrastructure.security.CustomAuthenticationDetails;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener el token JWT de la cabecera Authorization
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        try {
            // Si el token está presente, procesarlo
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); // Obtener el token (sin "Bearer ")
                username = jwtUtil.extractUsername(jwt); // Extraer el nombre de usuario del token
            }

            // Si el token es válido y el usuario está autenticado
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Verificar si el JWT es válido
                if (jwtUtil.validateToken(jwt, username)) {
                    // Cargar detalles del usuario desde el servicio de autenticación
                    var userDetails = jwtAuthenticationService.loadUserByUsername(username);


                    // Crear un token de autenticación con roles y permisos
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // Establecer las autoridades (roles + permisos)
                    );

                    // Establecer los detalles de autenticación
                    // authenticationToken.setDetails(new
                    // WebAuthenticationDetailsSource().buildDetails(request));
                    authenticationToken.setDetails(
                            new CustomAuthenticationDetails(userDetails.getId(), userDetails.getUsername()));

                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Manejar token expirado
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"TokenExpired\", \"message\": \"El token ha expirado. Por favor, inicie sesión nuevamente.\"}");
        } catch (Exception e) {
            // Manejo de excepciones genéricas
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\": \"Unauthorized\", \"message\": \"No tienes permiso para acceder a este recurso. :\"}"+e);
        }
    }

}
