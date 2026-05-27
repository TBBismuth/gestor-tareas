package com.tugestor.gestortareas.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class PreferenciasNotificacion {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idPreferenciasNotificacion;
	@OneToOne
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private Usuario usuario;
	@Column(nullable = false)
	private boolean notificacionesActivas = true;
	@Column(nullable = false)
	private boolean aviso24hActivo = true;
	@ElementCollection(targetClass = Prioridad.class)
	@CollectionTable(name = "preferencias_notificacion_prioridades_aviso_24h",
			joinColumns = @JoinColumn(name = "preferencias_notificacion_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "prioridad", nullable = false)
	private Set<Prioridad> prioridadesAviso24h = new HashSet<>();
	@ManyToMany
	@JoinTable(
			name = "preferencias_notificacion_grupos_aviso_24h",
			joinColumns = @JoinColumn(name = "preferencias_notificacion_id"),
			inverseJoinColumns = @JoinColumn(name = "grupo_id")
	)
	private Set<Grupo> gruposAviso24h = new HashSet<>();
	@Column(nullable = false)
	private boolean recordatorioInteligenteActivo = true;
	@Column(nullable = false)
	private boolean notificarAsignacionesGrupoActivo = true;

	public PreferenciasNotificacion() {
	}

	public Long getIdPreferenciasNotificacion() {
		return idPreferenciasNotificacion;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	public Set<Prioridad> getPrioridadesAviso24h() {
		return prioridadesAviso24h;
	}
	public void setPrioridadesAviso24h(Set<Prioridad> prioridadesAviso24h) {
		this.prioridadesAviso24h = prioridadesAviso24h;
	}
	public Set<Grupo> getGruposAviso24h() {
		return gruposAviso24h;
	}
	public void setGruposAviso24h(Set<Grupo> gruposAviso24h) {
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
