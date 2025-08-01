package com.tugestor.gestortareas.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UsuarioRepository ur;
	public CustomUserDetailsService (UsuarioRepository ur) {
		this.ur= ur;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return ur.findByEmail(username)	// Devuelve un Optional<Usuario>
				.map(usuario -> org.springframework.security.core.userdetails.User.builder()
						.username(usuario.getEmail())	// Si existe construyo un User de Spring
						.password(usuario.getPassword())
						.roles("USER")
						.build())						// Si no existe lanzamos excepcion
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
	}

}
