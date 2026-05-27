package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PushSubscripcion {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idPushSubscripcion;
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	@Column(nullable = false, unique = true, length = 2048)
	private String endpoint;
	@Column(nullable = false, length = 512)
	private String p256dh;
	@Column(nullable = false, length = 512)
	private String auth;
	@Column(length = 1000)
	private String userAgent;
	@Column(length = 100)
	private String nombreDispositivo;
	@Column(nullable = false)
	private boolean activa = true;
	private LocalDateTime fechaAlta = LocalDateTime.now();
	private LocalDateTime fechaUltimoUso;
	private LocalDateTime fechaBaja;

	public PushSubscripcion() {
	}

	public Long getIdPushSubscripcion() {
		return idPushSubscripcion;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getP256dh() {
		return p256dh;
	}
	public void setP256dh(String p256dh) {
		this.p256dh = p256dh;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getNombreDispositivo() {
		return nombreDispositivo;
	}
	public void setNombreDispositivo(String nombreDispositivo) {
		this.nombreDispositivo = nombreDispositivo;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public LocalDateTime getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(LocalDateTime fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public LocalDateTime getFechaUltimoUso() {
		return fechaUltimoUso;
	}
	public void setFechaUltimoUso(LocalDateTime fechaUltimoUso) {
		this.fechaUltimoUso = fechaUltimoUso;
	}
	public LocalDateTime getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(LocalDateTime fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
}
