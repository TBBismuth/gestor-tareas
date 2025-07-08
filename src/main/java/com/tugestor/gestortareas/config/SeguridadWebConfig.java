//package com.tugestor.gestortareas.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
///**
// * Clase de configuración de seguridad para Spring Security.
// * En esta configuración se desactiva la seguridad por defecto para permitir el acceso libre
// * a todos los endpoints de la API durante la fase de desarrollo.
// */
//@Configuration
//public class SeguridadWebConfig {
//	// Define la cadena de filtros de seguridad utilizada por Spring Security.
//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http
//		// Permite todas las solicitudes HTTP sin autenticación
//		.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//		// Desactiva la protección CSRF (Cross-Site Request Forgery)
//		.csrf(csrf -> csrf.disable());
//		// Construye y devuelve la cadena de filtros de seguridad
//		return http.build();
//	}
//}
