package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;

public class AsignacionGrupoListadoResponse {
	private Long idAsignacionGrupo;
	private Long idGrupo;
	private Long idCreadorAsignacion;
	private String nombreCreadorAsignacion;
	private String titulo;
	private Prioridad prioridad;
	private int tiempo;
	private LocalDateTime fechaEntrega;
	private LocalDateTime fechaCreacion;
	private TipoAsignacionGrupo tipoAsignacion;
	private int totalDestinatarios;

	public AsignacionGrupoListadoResponse() {
	}
	public AsignacionGrupoListadoResponse(AsignacionGrupo asignacion, int totalDestinatarios) {
		this.idAsignacionGrupo = asignacion.getIdAsignacionGrupo();
		this.idGrupo = asignacion.getGrupo() != null ? asignacion.getGrupo().getIdGrupo() : null;
		if (asignacion.getCreadorAsignacion() != null) {
			this.idCreadorAsignacion = asignacion.getCreadorAsignacion().getIdUsuario();
			this.nombreCreadorAsignacion = asignacion.getCreadorAsignacion().getNombre();
		}
		this.titulo = asignacion.getTitulo();
		this.prioridad = asignacion.getPrioridad();
		this.tiempo = asignacion.getTiempo();
		this.fechaEntrega = asignacion.getFechaEntrega();
		this.fechaCreacion = asignacion.getFechaCreacion();
		this.tipoAsignacion = asignacion.getTipoAsignacion();
		this.totalDestinatarios = totalDestinatarios;
	}

	public Long getIdAsignacionGrupo() {
		return idAsignacionGrupo;
	}
	public Long getIdGrupo() {
		return idGrupo;
	}
	public Long getIdCreadorAsignacion() {
		return idCreadorAsignacion;
	}
	public String getNombreCreadorAsignacion() {
		return nombreCreadorAsignacion;
	}
	public String getTitulo() {
		return titulo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public int getTiempo() {
		return tiempo;
	}
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public TipoAsignacionGrupo getTipoAsignacion() {
		return tipoAsignacion;
	}
	public int getTotalDestinatarios() {
		return totalDestinatarios;
	}
}
