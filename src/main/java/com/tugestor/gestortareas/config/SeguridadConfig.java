package com.tugestor.gestortareas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
//	private final JwtAuthenticationFilter jwtAuthenticationFilter;
//	public SeguridadConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
		http.csrf(csrf -> csrf.disable())	// Desactiva CSRF porque mi API es stateless (sin sesiones)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/usuario/add", "api/usuario/login").permitAll()	// Solo registro y login sin autentificacion
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