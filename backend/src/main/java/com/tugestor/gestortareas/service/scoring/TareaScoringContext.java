package com.tugestor.gestortareas.service.scoring;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.OrigenTareaFiltro;
import com.tugestor.gestortareas.model.Tarea;

public class TareaScoringContext {
	private final Tarea tarea;
	private final OrigenTareaFiltro origen;
	private final AsignacionGrupoMiembro asignacionGrupoMiembro;
	
	public TareaScoringContext(Tarea tarea, OrigenTareaFiltro origen,
			AsignacionGrupoMiembro asignacionGrupoMiembro) {
		this.tarea = tarea;
		this.asignacionGrupoMiembro = asignacionGrupoMiembro;
		this.origen = normalizarOrigen(origen, asignacionGrupoMiembro);
	}
	
	public static TareaScoringContext personal(Tarea tarea) {
		return new TareaScoringContext(tarea, OrigenTareaFiltro.PERSONAL, null);
	}
	
	public static TareaScoringContext grupo(AsignacionGrupoMiembro asignacionGrupoMiembro) {
		Tarea tarea = asignacionGrupoMiembro != null ? asignacionGrupoMiembro.getTareaGenerada() : null;
		return new TareaScoringContext(tarea, OrigenTareaFiltro.GRUPO, asignacionGrupoMiembro);
	}
	
	public Tarea getTarea() {
		return tarea;
	}
	
	public OrigenTareaFiltro getOrigen() {
		return origen;
	}
	
	public AsignacionGrupoMiembro getAsignacionGrupoMiembro() {
		return asignacionGrupoMiembro;
	}
	
	public boolean esPersonal() {
		return origen == OrigenTareaFiltro.PERSONAL;
	}
	
	public boolean esGrupo() {
		return origen == OrigenTareaFiltro.GRUPO || asignacionGrupoMiembro != null;
	}
	
	public boolean tieneAsignacionGrupo() {
		return asignacionGrupoMiembro != null;
	}
	
	public EstadoRevisionAsignacion getEstadoRevisionAsignacion() {
		return asignacionGrupoMiembro != null ? asignacionGrupoMiembro.getEstadoRevision() : null;
	}
	
	public boolean estaEntregada() {
		return getEstadoRevisionAsignacion() == EstadoRevisionAsignacion.ENTREGADA;
	}
	
	public boolean estaValidada() {
		return getEstadoRevisionAsignacion() == EstadoRevisionAsignacion.VALIDADA;
	}
	
	public boolean estaReabierta() {
		return getEstadoRevisionAsignacion() == EstadoRevisionAsignacion.REABIERTA;
	}
	
	public boolean sinRevision() {
		return getEstadoRevisionAsignacion() == null;
	}
	
	private OrigenTareaFiltro normalizarOrigen(OrigenTareaFiltro origen,
			AsignacionGrupoMiembro asignacionGrupoMiembro) {
		if (asignacionGrupoMiembro != null || origen == OrigenTareaFiltro.GRUPO) {
			return OrigenTareaFiltro.GRUPO;
		}
		return OrigenTareaFiltro.PERSONAL;
	}
}
