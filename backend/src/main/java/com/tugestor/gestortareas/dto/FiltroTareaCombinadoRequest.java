package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.CriterioOrdenTareaCombinado;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.OrigenTareaFiltro;
import com.tugestor.gestortareas.model.Prioridad;

public class FiltroTareaCombinadoRequest {
	private OrigenTareaFiltro origen = OrigenTareaFiltro.TODAS;
	private Long idGrupo;
	private Prioridad prioridad;
	private Estado estado;
	private CriterioOrdenTareaCombinado criterioOrdenActivo = CriterioOrdenTareaCombinado.FECHA_AGREGADO;

	public FiltroTareaCombinadoRequest() {
	}

	public OrigenTareaFiltro getOrigen() {
		return origen;
	}
	public void setOrigen(OrigenTareaFiltro origen) {
		this.origen = origen;
	}
	public Long getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public CriterioOrdenTareaCombinado getCriterioOrdenActivo() {
		return criterioOrdenActivo;
	}
	public void setCriterioOrdenActivo(CriterioOrdenTareaCombinado criterioOrdenActivo) {
		this.criterioOrdenActivo = criterioOrdenActivo;
	}
}
