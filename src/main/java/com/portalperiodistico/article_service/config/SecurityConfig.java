package com.portalperiodistico.article_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracion de seguridad para el article-service.
 * Solo valida JWTs, no maneja autenticacion por contrasena.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario para APIs stateless)
                .csrf(csrf -> csrf.disable())

                // Sin sesiones (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configurar autorizacion de endpoints
                .authorizeHttpRequests(authz -> authz
                        // GET publicos
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/articles/**").permitAll()

                        // POST, PUT, DELETE requieren autenticacion
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/articles/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/articles/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/articles/**").authenticated()

                        // Cualquier otra ruta es publica
                        .anyRequest().permitAll()
                )

                // Agregar nuestro filtro JWT antes del filtro de autenticacion estandar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}