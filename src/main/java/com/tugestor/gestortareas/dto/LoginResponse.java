package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.Usuario;

public class LoginResponse {
	private Long idUsuario;
	private String nombre;
	private String email;
	
	public LoginResponse() {
	}
	public LoginResponse(Long idUsuario, String nombre, String email) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.email = email;
	}
	public LoginResponse(Usuario usuario) {
		this.idUsuario = usuario.getIdUsuario();
		this.nombre = usuario.getNombre();
		this.email = usuario.getEmail();
	}
	
	public Long getIdUsuario() {
		return idUsuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
