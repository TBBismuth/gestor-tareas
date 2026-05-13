package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class AsignacionGrupoMiembro {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idAsignacionGrupoMiembro;
	@ManyToOne
	@JoinColumn(name = "asignacion_grupo_id", nullable = false)
	private AsignacionGrupo asignacionGrupo;
	@ManyToOne
	@JoinColumn(name = "usuario_miembro_id", nullable = false)
	private Usuario usuarioMiembro;
	@OneToOne
	@JoinColumn(name = "tarea_generada_id", nullable = false, unique = true)
	private Tarea tareaGenerada;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoRevisionAsignacion estadoRevision = EstadoRevisionAsignacion.PENDIENTE;
	private LocalDateTime fechaAsignacion = LocalDateTime.now();
	private LocalDateTime fechaEntregaInicial;
	private LocalDateTime fechaEntregaActual;

	public AsignacionGrupoMiembro() {
		// Constructor por defecto para JPA
	}

	public Long getIdAsignacionGrupoMiembro() {
		return idAsignacionGrupoMiembro;
	}
	public AsignacionGrupo getAsignacionGrupo() {
		return asignacionGrupo;
	}
	public void setAsignacionGrupo(AsignacionGrupo asignacionGrupo) {
		this.asignacionGrupo = asignacionGrupo;
	}
	public Usuario getUsuarioMiembro() {
		return usuarioMiembro;
	}
	public void setUsuarioMiembro(Usuario usuarioMiembro) {
		this.usuarioMiembro = usuarioMiembro;
	}
	public Tarea getTareaGenerada() {
		return tareaGenerada;
	}
	public void setTareaGenerada(Tarea tareaGenerada) {
		this.tareaGenerada = tareaGenerada;
	}
	public EstadoRevisionAsignacion getEstadoRevision() {
		return estadoRevision;
	}
	public void setEstadoRevision(EstadoRevisionAsignacion estadoRevision) {
		this.estadoRevision = estadoRevision;
	}
	public LocalDateTime getFechaAsignacion() {
		return fechaAsignacion;
	}
	public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}
	public LocalDateTime getFechaEntregaInicial() {
		return fechaEntregaInicial;
	}
	public void setFechaEntregaInicial(LocalDateTime fechaEntregaInicial) {
		this.fechaEntregaInicial = fechaEntregaInicial;
	}
	public LocalDateTime getFechaEntregaActual() {
		return fechaEntregaActual;
	}
	public void setFechaEntregaActual(LocalDateTime fechaEntregaActual) {
		this.fechaEntregaActual = fechaEntregaActual;
	}
}
