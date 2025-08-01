package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.Usuario;

public class UsuarioResponse {
	private Long idUsuario;
	private String nombre;
	private String email;
	private LocalDateTime fechaRegistro;
	private boolean activo;
	private boolean verificado;
	
	public UsuarioResponse() {
	}
	public UsuarioResponse(Long idUsuario, String nombre, String email, LocalDateTime fechaRegistro, boolean activo,
			boolean verificado) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.email = email;
		this.fechaRegistro = fechaRegistro;
		this.activo = activo;
		this.verificado = verificado;
	}
	public UsuarioResponse(Usuario usuario) {
		this.idUsuario = usuario.getIdUsuario();
		this.nombre = usuario.getNombre();
		this.email = usuario.getEmail();
		this.fechaRegistro = usuario.getFechaRegistro();
		this.activo = usuario.isActivo();
		this.verificado = usuario.isVerificado();
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
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public boolean isVerificado() {
		return verificado;
	}
	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}
}
