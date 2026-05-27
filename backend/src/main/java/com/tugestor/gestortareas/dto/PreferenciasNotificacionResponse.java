package com.tugestor.gestortareas.dto;

import java.util.Comparator;
import java.util.List;

import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.PreferenciasNotificacion;
import com.tugestor.gestortareas.model.Prioridad;

public class PreferenciasNotificacionResponse {
	private Long idPreferenciasNotificacion;
	private boolean notificacionesActivas;
	private boolean aviso24hActivo;
	private List<Prioridad> prioridadesAviso24h;
	private List<Long> gruposAviso24h;
	private boolean recordatorioInteligenteActivo;
	private boolean notificarAsignacionesGrupoActivo;

	public PreferenciasNotificacionResponse() {
	}
	public PreferenciasNotificacionResponse(PreferenciasNotificacion preferencias) {
		this.idPreferenciasNotificacion = preferencias.getIdPreferenciasNotificacion();
		this.notificacionesActivas = preferencias.isNotificacionesActivas();
		this.aviso24hActivo = preferencias.isAviso24hActivo();
		this.prioridadesAviso24h = preferencias.getPrioridadesAviso24h().stream()
				.sorted(Comparator.comparingInt(Prioridad::ordinal))
				.toList();
		this.gruposAviso24h = preferencias.getGruposAviso24h().stream()
				.map(Grupo::getIdGrupo)
				.sorted()
				.toList();
		this.recordatorioInteligenteActivo = preferencias.isRecordatorioInteligenteActivo();
		this.notificarAsignacionesGrupoActivo = preferencias.isNotificarAsignacionesGrupoActivo();
	}

	public Long getIdPreferenciasNotificacion() {
		return idPreferenciasNotificacion;
	}
	public void setIdPreferenciasNotificacion(Long idPreferenciasNotificacion) {
		this.idPreferenciasNotificacion = idPreferenciasNotificacion;
	}
	public boolean isNotificacionesActivas() {
		return notificacionesActivas;
	}
	public void setNotificacionesActivas(boolean notificacionesActivas) {
		this.notificacionesActivas = notificacionesActivas;
	}
	public boolean isAviso24hActivo() {
		return aviso24hActivo;
	}
	public void setAviso24hActivo(boolean aviso24hActivo) {
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
	public boolean isRecordatorioInteligenteActivo() {
		return recordatorioInteligenteActivo;
	}
	public void setRecordatorioInteligenteActivo(boolean recordatorioInteligenteActivo) {
		this.recordatorioInteligenteActivo = recordatorioInteligenteActivo;
	}
	public boolean isNotificarAsignacionesGrupoActivo() {
		return notificarAsignacionesGrupoActivo;
	}
	public void setNotificarAsignacionesGrupoActivo(boolean notificarAsignacionesGrupoActivo) {
		this.notificarAsignacionesGrupoActivo = notificarAsignacionesGrupoActivo;
	}
}
