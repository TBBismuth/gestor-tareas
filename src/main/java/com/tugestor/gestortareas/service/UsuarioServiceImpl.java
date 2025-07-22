package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.exception.EmailDuplicadoException;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioServiceImpl implements UsuarioService{
	private final UsuarioRepository ur;
	private final PasswordEncoder pe;
	public UsuarioServiceImpl(UsuarioRepository ur, PasswordEncoder pe) {
		this.ur = ur;
		this.pe = pe;
	}

	@Override
	public Usuario guardarUsuario(Usuario usuario) {
		ur.findByEmail(usuario.getEmail()).ifPresent(temp -> {
			throw new EmailDuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
		});
		usuario.setPassword(pe.encode(usuario.getPassword()));
		return ur.save(usuario);
	}

	@Override
	public Optional<Usuario> obtenerPorEmail(String email) {
		return ur.findByEmail(email);
	}

	@Override
	public Usuario obtenerPorId(Long id) {
		return ur.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
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
	public LoginResponse login(LoginRequest login) {
		Usuario usuario = ur.findByEmail(login.getEmail())
				.orElseThrow(() -> new RuntimeException("No existe un usuario con el email: " + login.getEmail()));
		if (!pe.matches(login.getPassword(), usuario.getPassword())) { //El orden de .matches importa, primero raw text y luego encoded text
			throw new RuntimeException("La contraseña no coincide");
		}
		return new LoginResponse(usuario);
	}

	@Override
	public Usuario guardarUsuario(UsuarioRequest usuarioRequest) {
		Usuario usuario = new Usuario(
				usuarioRequest.getNombre(),
				usuarioRequest.getEmail(),
				usuarioRequest.getPassword(),
				true,
				false);
		
		ur.findByEmail(usuario.getEmail()).ifPresent(temp -> {
			throw new EmailDuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
		});
		usuario.setPassword(pe.encode(usuario.getPassword()));
		return ur.save(usuario);
	}

}
