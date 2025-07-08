package com.tugestor.gestortareas.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtS;
	private final UserDetailsService uds;
	public JwtAuthenticationFilter (JwtService jwtS, UserDetailsService uds) {
		this.jwtS = jwtS;
		this.uds = uds;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");	// Extraigo la linea de autorización
		final String jwt;
		final String username;
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {	// Si no hay linea de autorizacion o no 
			filterChain.doFilter(request, response);			// empieza por Baerer dejamos pasar sin hacer nada
			return;
		}
		jwt = authHeader.substring(7);			// Eliminamos el "Baerer "
		username = jwtS.extractUsername(jwt);	// Saco el usuario (subject)
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// si tenemos un username y no está previamente autentificado entramos
			UserDetails userDetails = this.uds.loadUserByUsername(username);
			// Carga el usuario de la base de datos y devuelvo un UserDetails completo
			if (jwtS.isTokenValid(jwt, userDetails)) {// Si el token es valido, no ha expirado y el username coincide
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails,
								null,
								userDetails.getAuthorities()
								);
				// authToken es un objeto que Spring utiliza para representar un usuario autenticado
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// Pone los detalles de la peticion
				SecurityContextHolder.getContext().setAuthentication(authToken);
				// Guardo la peticion en el SecutiryContextHolder
			}
		}
		filterChain.doFilter(request, response);
	}

}
