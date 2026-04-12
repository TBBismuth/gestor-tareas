package com.tugestor.gestortareas.service;

import com.tugestor.gestortareas.dto.LoginRequest;
import com.tugestor.gestortareas.dto.LoginResponse;
import com.tugestor.gestortareas.dto.UsuarioRequest;
import com.tugestor.gestortareas.model.Usuario;

public interface UsuarioService {
	/**
	 * @deprecated Use {@link #guardarUsuario(UsuarioRequest)} instead
	 */
	@Deprecated(since="2.0", forRemoval=false)
	Usuario guardarUsuario(Usuario usuario);
	Usuario guardarUsuario(UsuarioRequest usuarioRequest);
	Usuario obtenerUsuarioActual();
	void eliminarUsuarioActual();
	LoginResponse login (LoginRequest login);
}
