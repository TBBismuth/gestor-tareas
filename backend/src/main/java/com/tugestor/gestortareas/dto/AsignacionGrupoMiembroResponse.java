package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;

public class AsignacionGrupoMiembroResponse {
	private Long idAsignacionGrupoMiembro;
	private Long idUsuarioMiembro;
	private String nombreUsuarioMiembro;
	private String emailUsuarioMiembro;
	private Long idTareaGenerada;
	private EstadoRevisionAsignacion estadoRevision;
	private LocalDateTime fechaAsignacion;
	private LocalDateTime fechaEntregaInicial;
	private LocalDateTime fechaEntregaActual;

	public AsignacionGrupoMiembroResponse() {
	}
	public AsignacionGrupoMiembroResponse(AsignacionGrupoMiembro miembro) {
		this.idAsignacionGrupoMiembro = miembro.getIdAsignacionGrupoMiembro();
		if (miembro.getUsuarioMiembro() != null) {
			this.idUsuarioMiembro = miembro.getUsuarioMiembro().getIdUsuario();
			this.nombreUsuarioMiembro = miembro.getUsuarioMiembro().getNombre();
			this.emailUsuarioMiembro = miembro.getUsuarioMiembro().getEmail();
		}
		this.idTareaGenerada = miembro.getTareaGenerada() != null ? miembro.getTareaGenerada().getIdTarea() : null;
		this.estadoRevision = miembro.getEstadoRevision();
		this.fechaAsignacion = miembro.getFechaAsignacion();
		this.fechaEntregaInicial = miembro.getFechaEntregaInicial();
		this.fechaEntregaActual = miembro.getFechaEntregaActual();
	}

	public Long getIdAsignacionGrupoMiembro() {
		return idAsignacionGrupoMiembro;
	}
	public Long getIdUsuarioMiembro() {
		return idUsuarioMiembro;
	}
	public String getNombreUsuarioMiembro() {
		return nombreUsuarioMiembro;
	}
	public String getEmailUsuarioMiembro() {
		return emailUsuarioMiembro;
	}
	public Long getIdTareaGenerada() {
		return idTareaGenerada;
	}
	public EstadoRevisionAsignacion getEstadoRevision() {
		return estadoRevision;
	}
	public LocalDateTime getFechaAsignacion() {
		return fechaAsignacion;
	}
	public LocalDateTime getFechaEntregaInicial() {
		return fechaEntregaInicial;
	}
	public LocalDateTime getFechaEntregaActual() {
		return fechaEntregaActual;
	}
}
