package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService{
	private final UsuarioRepository ur;
	private final PasswordEncoder ps;
	public UsuarioServiceImpl(UsuarioRepository ur, PasswordEncoder ps) {
		this.ur = ur;
		this.ps = ps;
	}

	@Override
	public Usuario guardarUsuario(Usuario usuario) {
		ur.findByEmail(usuario.getEmail()).ifPresent(temp -> {
			throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
		});
		usuario.setPassword(ps.encode(usuario.getPassword()));
		return ur.save(usuario);
	}

	@Override
	public Optional<Usuario> obtenerPorEmail(String email) {
		return ur.findByEmail(email);
	}

	@Override
	public Usuario obtenerPorId(Long id) {
		return ur.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
	}

	@Override
	public List<Usuario> obtenerTodos() {
		return ur.findAll();
	}

	@Override
	public void eliminarPorId(Long id) {
		Usuario usuario = ur.findById(id)
				.orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
		ur.delete(usuario);
	}

	@Override
	public Usuario login(LoginRequest login) {
		Usuario usuario = ur.findByEmail(login.getEmail())
				.orElseThrow(() -> new RuntimeException("No existe un usuario con el email: " + login.getEmail()));
		if (!ps.matches(login.getPassword(), usuario.getPassword())) { //El orden de .matches importa, primero raw text y luego encoded text
			throw new RuntimeException("La contraseña no coincide");
		}
		return usuario;
	}

}
