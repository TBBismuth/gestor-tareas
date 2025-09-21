package com.tugestor.gestortareas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tugestor.gestortareas.security.JwtAuthenticationFilter;
import com.tugestor.gestortareas.security.JwtService;

@Configuration
public class SeguridadConfig {	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
		http.cors(Customizer.withDefaults())	// Habilita CORS con la configuracion de CorsConfig
		.csrf(csrf -> csrf.disable())	// Desactiva CSRF porque mi API es stateless (sin sesiones)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(
						HttpMethod.OPTIONS,
						"/api/**"
						).permitAll()
				.requestMatchers(
						"/api/usuario/add",
						"/api/usuario/login",
						"/swagger-ui/**",
						"/v3/api-docs",
						"/v3/api-docs/**",
						"/swagger-resources/**",
						"/swagger-ui.html",
						"/webjars/**",
						"/documentacion-api",
						"/documentacion-api/**"
						).permitAll()// Solo registro, login y documentacion sin autentificacion
				.anyRequest().authenticated()											// el resto rquiere JWT
		)
		.sessionManagement(session -> session		// Indica que NO se creará sesion de usuario y cada peticion es stateless
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		)
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService uds) {
		return new JwtAuthenticationFilter(jwtService, uds);
	}
	
}