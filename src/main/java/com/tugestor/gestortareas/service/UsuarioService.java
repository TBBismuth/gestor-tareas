package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Optional;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.model.Usuario;

public interface UsuarioService {
	Usuario guardarUsuario(Usuario usuario);
	Usuario guardarUsuario(UsuarioRequest usuarioRequest);
	Optional<Usuario> obtenerPorEmail(String email);
	Usuario obtenerPorId(Long id);
	List<Usuario> obtenerTodos();
	void eliminarPorId(Long id);
	LoginResponse login (LoginRequest login);
}
