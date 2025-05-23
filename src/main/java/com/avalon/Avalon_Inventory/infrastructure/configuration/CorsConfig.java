package com.avalon.Avalon_Inventory.infrastructure.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // Configuración de CORS
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Permitir todas las solicitudes desde cualquier origen (aquí se puede
        // restringir a orígenes específicos)
        corsConfiguration.setAllowedOrigins(Arrays.asList("*")); // Se puede cambiar a una lista de orígenes permitidos
                                                                 // EJ: /http://localhost:4200

        // Permitir métodos HTTP específicos (GET, POST, PUT, DELETE, etc.)
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        // Permitir todos los encabezados en las solicitudes
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Configuración de CORS basada en URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}