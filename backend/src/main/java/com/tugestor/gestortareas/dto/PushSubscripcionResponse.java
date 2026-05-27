package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.PushSubscripcion;

public class PushSubscripcionResponse {
	private Long idPushSubscripcion;
	private boolean activa;
	private String userAgent;
	private String nombreDispositivo;
	private LocalDateTime fechaAlta;
	private LocalDateTime fechaUltimoUso;
	private LocalDateTime fechaBaja;

	public PushSubscripcionResponse() {
	}
	public PushSubscripcionResponse(PushSubscripcion pushSubscripcion) {
		this.idPushSubscripcion = pushSubscripcion.getIdPushSubscripcion();
		this.activa = pushSubscripcion.isActiva();
		this.userAgent = pushSubscripcion.getUserAgent();
		this.nombreDispositivo = pushSubscripcion.getNombreDispositivo();
		this.fechaAlta = pushSubscripcion.getFechaAlta();
		this.fechaUltimoUso = pushSubscripcion.getFechaUltimoUso();
		this.fechaBaja = pushSubscripcion.getFechaBaja();
	}

	public Long getIdPushSubscripcion() {
		return idPushSubscripcion;
	}
	public void setIdPushSubscripcion(Long idPushSubscripcion) {
		this.idPushSubscripcion = idPushSubscripcion;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
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
