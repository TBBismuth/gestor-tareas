package com.tugestor.gestortareas.dto;

import java.util.List;

import com.tugestor.gestortareas.model.Prioridad;

import jakarta.validation.constraints.NotNull;

public class PreferenciasNotificacionRequest {
	@NotNull(message = "El interruptor general de notificaciones no puede ser nulo")
	private Boolean notificacionesActivas;
	@NotNull(message = "El aviso 24h no puede ser nulo")
	private Boolean aviso24hActivo;
	private List<@NotNull(message = "La prioridad no puede ser nula") Prioridad> prioridadesAviso24h;
	private List<@NotNull(message = "El grupo no puede ser nulo") Long> gruposAviso24h;
	@NotNull(message = "El recordatorio inteligente no puede ser nulo")
	private Boolean recordatorioInteligenteActivo;
	@NotNull(message = "La notificacion de asignaciones de grupo no puede ser nula")
	private Boolean notificarAsignacionesGrupoActivo;

	public Boolean getNotificacionesActivas() {
		return notificacionesActivas;
	}
	public void setNotificacionesActivas(Boolean notificacionesActivas) {
		this.notificacionesActivas = notificacionesActivas;
	}
	public Boolean getAviso24hActivo() {
		return aviso24hActivo;
	}
	public void setAviso24hActivo(Boolean aviso24hActivo) {
		this.aviso24hActivo = aviso24hActivo;
	}
	public List<Prioridad> getPrioridadesAviso24h() {
		return prioridadesAviso24h;
	}
	public void setPrioridadesAviso24h(List<Prioridad> prioridadesAviso24h) {
		this.prioridadesAviso24h = prioridadesAviso24h;
	}
	public List<Long> getGruposAviso24h() {
		return gruposAviso24h;
	}
	public void setGruposAviso24h(List<Long> gruposAviso24h) {
		this.gruposAviso24h = gruposAviso24h;
	}
	public Boolean getRecordatorioInteligenteActivo() {
		return recordatorioInteligenteActivo;
	}
	public void setRecordatorioInteligenteActivo(Boolean recordatorioInteligenteActivo) {
		this.recordatorioInteligenteActivo = recordatorioInteligenteActivo;
	}
	public Boolean getNotificarAsignacionesGrupoActivo() {
		return notificarAsignacionesGrupoActivo;
	}
	public void setNotificarAsignacionesGrupoActivo(Boolean notificarAsignacionesGrupoActivo) {
		this.notificarAsignacionesGrupoActivo = notificarAsignacionesGrupoActivo;
	}
}
