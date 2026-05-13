package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;

public class AsignacionGrupoResponse {
	private Long idAsignacionGrupo;
	private Long idGrupo;
	private String nombreGrupo;
	private Long idCreadorAsignacion;
	private String nombreCreadorAsignacion;
	private String titulo;
	private String descripcion;
	private Prioridad prioridad;
	private int tiempo;
	private LocalDateTime fechaEntrega;
	private LocalDateTime fechaCreacion;
	private TipoAsignacionGrupo tipoAsignacion;
	private int totalDestinatarios;
	private List<AsignacionGrupoMiembroResponse> destinatarios;

	public AsignacionGrupoResponse() {
	}
	public AsignacionGrupoResponse(AsignacionGrupo asignacion, List<AsignacionGrupoMiembro> miembros) {
		this.idAsignacionGrupo = asignacion.getIdAsignacionGrupo();
		if (asignacion.getGrupo() != null) {
			this.idGrupo = asignacion.getGrupo().getIdGrupo();
			this.nombreGrupo = asignacion.getGrupo().getNombre();
		}
		if (asignacion.getCreadorAsignacion() != null) {
			this.idCreadorAsignacion = asignacion.getCreadorAsignacion().getIdUsuario();
			this.nombreCreadorAsignacion = asignacion.getCreadorAsignacion().getNombre();
		}
		this.titulo = asignacion.getTitulo();
		this.descripcion = asignacion.getDescripcion();
		this.prioridad = asignacion.getPrioridad();
		this.tiempo = asignacion.getTiempo();
		this.fechaEntrega = asignacion.getFechaEntrega();
		this.fechaCreacion = asignacion.getFechaCreacion();
		this.tipoAsignacion = asignacion.getTipoAsignacion();
		this.totalDestinatarios = miembros != null ? miembros.size() : 0;
		this.destinatarios = miembros != null ? miembros.stream().map(AsignacionGrupoMiembroResponse::new).toList() : List.of();
	}

	public Long getIdAsignacionGrupo() {
		return idAsignacionGrupo;
	}
	public Long getIdGrupo() {
		return idGrupo;
	}
	public String getNombreGrupo() {
		return nombreGrupo;
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
	public String getDescripcion() {
		return descripcion;
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
	public List<AsignacionGrupoMiembroResponse> getDestinatarios() {
		return destinatarios;
	}
}
