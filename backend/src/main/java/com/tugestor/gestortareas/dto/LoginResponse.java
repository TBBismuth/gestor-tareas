package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.Usuario;

public class LoginResponse {
	private Long idUsuario;
	private String nombre;
	private String email;
	private String token;
	private String accessToken;
	private String refreshToken;
	
	public LoginResponse() {
	}
	public LoginResponse(Long idUsuario, String nombre, String email, String token) {
		this(idUsuario, nombre, email, token, null);
	}
	public LoginResponse(Long idUsuario, String nombre, String email, String accessToken, String refreshToken) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.email = email;
		this.token = accessToken;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
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
		this.accessToken = token;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		this.token = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
