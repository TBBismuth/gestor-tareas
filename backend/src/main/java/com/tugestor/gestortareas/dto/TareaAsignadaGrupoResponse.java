package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;

public class TareaAsignadaGrupoResponse {
	private Long idTarea;
	private String titulo;
	private String descripcion;
	private int tiempo;
	private Prioridad prioridad;
	private LocalDateTime fechaEntrega;
	private LocalDateTime fechaAgregado;
	private boolean completada;
	private LocalDateTime fechaCompletada;
	private String estado;
	private Long idAsignacionGrupoMiembro;
	private Long idAsignacionGrupo;
	private Long idGrupoOrigen;
	private String nombreGrupoOrigen;
	private TipoAsignacionGrupo tipoAsignacion;
	private EstadoRevisionAsignacion estadoRevisionAsignacion;
	private String comentarioRevision;
	private LocalDateTime fechaAsignacion;
	private LocalDateTime fechaEntregaInicial;
	private LocalDateTime fechaEntregaActual;
	private LocalDateTime fechaRevision;

	public TareaAsignadaGrupoResponse() {
	}
	public TareaAsignadaGrupoResponse(AsignacionGrupoMiembro asignacionMiembro) {
		Tarea tarea = asignacionMiembro.getTareaGenerada();
		if (tarea != null) {
			this.idTarea = tarea.getIdTarea();
			this.titulo = tarea.getTitulo();
			this.descripcion = tarea.getDescripcion();
			this.tiempo = tarea.getTiempo();
			this.prioridad = tarea.getPrioridad();
			this.fechaEntrega = tarea.getFechaEntrega();
			this.fechaAgregado = tarea.getFechaAgregado();
			this.completada = tarea.isCompletada();
			this.fechaCompletada = tarea.getFechaCompletada();
			this.estado = tarea.getEstado().name();
		}
		this.idAsignacionGrupoMiembro = asignacionMiembro.getIdAsignacionGrupoMiembro();
		AsignacionGrupo asignacion = asignacionMiembro.getAsignacionGrupo();
		if (asignacion != null) {
			this.idAsignacionGrupo = asignacion.getIdAsignacionGrupo();
			this.tipoAsignacion = asignacion.getTipoAsignacion();
			Grupo grupo = asignacion.getGrupo();
			if (grupo != null) {
				this.idGrupoOrigen = grupo.getIdGrupo();
				this.nombreGrupoOrigen = grupo.getNombre();
			}
		}
		this.estadoRevisionAsignacion = asignacionMiembro.getEstadoRevision();
		this.comentarioRevision = asignacionMiembro.getComentarioRevision();
		this.fechaAsignacion = asignacionMiembro.getFechaAsignacion();
		this.fechaEntregaInicial = asignacionMiembro.getFechaEntregaInicial();
		this.fechaEntregaActual = asignacionMiembro.getFechaEntregaActual();
		this.fechaRevision = asignacionMiembro.getFechaRevision();
	}

	public Long getIdTarea() {
		return idTarea;
	}
	public String getTitulo() {
		return titulo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public int getTiempo() {
		return tiempo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public LocalDateTime getFechaAgregado() {
		return fechaAgregado;
	}
	public boolean isCompletada() {
		return completada;
	}
	public LocalDateTime getFechaCompletada() {
		return fechaCompletada;
	}
	public String getEstado() {
		return estado;
	}
	public Long getIdAsignacionGrupoMiembro() {
		return idAsignacionGrupoMiembro;
	}
	public Long getIdAsignacionGrupo() {
		return idAsignacionGrupo;
	}
	public Long getIdGrupoOrigen() {
		return idGrupoOrigen;
	}
	public String getNombreGrupoOrigen() {
		return nombreGrupoOrigen;
	}
	public TipoAsignacionGrupo getTipoAsignacion() {
		return tipoAsignacion;
	}
	public EstadoRevisionAsignacion getEstadoRevisionAsignacion() {
		return estadoRevisionAsignacion;
	}
	public String getComentarioRevision() {
		return comentarioRevision;
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
	public LocalDateTime getFechaRevision() {
		return fechaRevision;
	}
}
