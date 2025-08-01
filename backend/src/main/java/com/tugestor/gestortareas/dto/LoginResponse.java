package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.Usuario;

public class LoginResponse {
	private Long idUsuario;
	private String nombre;
	private String email;
	private String token;
	
	public LoginResponse() {
	}
	public LoginResponse(Long idUsuario, String nombre, String email, String token) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.email = email;
		this.token = token;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

}
