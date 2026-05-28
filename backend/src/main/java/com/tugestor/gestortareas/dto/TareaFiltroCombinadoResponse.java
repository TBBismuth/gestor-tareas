package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.AsignacionGrupo;
import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.OrigenTareaFiltro;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;

public class TareaFiltroCombinadoResponse {
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
	private Long idCategoria;
	private String nombreCategoria;
	private String colorCategoria;
	private String iconoCategoria;
	private OrigenTareaFiltro origenTarea;
	private Long idGrupoOrigen;
	private String nombreGrupoOrigen;
	private EstadoRevisionAsignacion estadoRevisionAsignacion;
	private String comentarioRevision;
	private TipoAsignacionGrupo tipoAsignacion;
	private boolean recordatorioInteligenteActivo;

	public TareaFiltroCombinadoResponse() {
	}

	public TareaFiltroCombinadoResponse(Tarea tarea) {
		this(tarea, false);
	}

	public TareaFiltroCombinadoResponse(Tarea tarea, boolean recordatorioInteligenteActivo) {
		cargarDatosTarea(tarea);
		this.origenTarea = OrigenTareaFiltro.PERSONAL;
		this.recordatorioInteligenteActivo = recordatorioInteligenteActivo;
	}

	public TareaFiltroCombinadoResponse(AsignacionGrupoMiembro asignacionMiembro) {
		this(asignacionMiembro, false);
	}

	public TareaFiltroCombinadoResponse(AsignacionGrupoMiembro asignacionMiembro,
			boolean recordatorioInteligenteActivo) {
		cargarDatosTarea(asignacionMiembro.getTareaGenerada());
		this.origenTarea = OrigenTareaFiltro.GRUPO;
		this.estadoRevisionAsignacion = asignacionMiembro.getEstadoRevision();
		this.comentarioRevision = asignacionMiembro.getComentarioRevision();
		this.recordatorioInteligenteActivo = recordatorioInteligenteActivo;

		AsignacionGrupo asignacion = asignacionMiembro.getAsignacionGrupo();
		if (asignacion != null) {
			this.tipoAsignacion = asignacion.getTipoAsignacion();
			Grupo grupo = asignacion.getGrupo();
			if (grupo != null) {
				this.idGrupoOrigen = grupo.getIdGrupo();
				this.nombreGrupoOrigen = grupo.getNombre();
			}
		}
	}

	private void cargarDatosTarea(Tarea tarea) {
		if (tarea == null) {
			return;
		}
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
		Categoria categoria = tarea.getCategoria();
		if (categoria != null) {
			this.idCategoria = categoria.getIdCategoria();
			this.nombreCategoria = categoria.getNombre();
			this.colorCategoria = categoria.getColor();
			this.iconoCategoria = categoria.getIcono();
		}
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
	public Long getIdCategoria() {
		return idCategoria;
	}
	public String getNombreCategoria() {
		return nombreCategoria;
	}
	public String getColorCategoria() {
		return colorCategoria;
	}
	public String getIconoCategoria() {
		return iconoCategoria;
	}
	public OrigenTareaFiltro getOrigenTarea() {
		return origenTarea;
	}
	public Long getIdGrupoOrigen() {
		return idGrupoOrigen;
	}
	public String getNombreGrupoOrigen() {
		return nombreGrupoOrigen;
	}
	public EstadoRevisionAsignacion getEstadoRevisionAsignacion() {
		return estadoRevisionAsignacion;
	}
	public String getComentarioRevision() {
		return comentarioRevision;
	}
	public TipoAsignacionGrupo getTipoAsignacion() {
		return tipoAsignacion;
	}
	public boolean isRecordatorioInteligenteActivo() {
		return recordatorioInteligenteActivo;
	}
	public void setRecordatorioInteligenteActivo(boolean recordatorioInteligenteActivo) {
		this.recordatorioInteligenteActivo = recordatorioInteligenteActivo;
	}
}
