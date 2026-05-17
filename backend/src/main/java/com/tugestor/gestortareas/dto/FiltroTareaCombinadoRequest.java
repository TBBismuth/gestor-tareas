package com.tugestor.gestortareas.dto;

import java.time.LocalDate;

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
	private String palabrasClave;
	private Integer tiempoMax;
	private Long idCategoria;
	private LocalDate fechaEntregaExacta;
	private LocalDate fechaEntregaHasta;

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
	public String getPalabrasClave() {
		return palabrasClave;
	}
	public void setPalabrasClave(String palabrasClave) {
		this.palabrasClave = palabrasClave;
	}
	public Integer getTiempoMax() {
		return tiempoMax;
	}
	public void setTiempoMax(Integer tiempoMax) {
		this.tiempoMax = tiempoMax;
	}
	public Long getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}
	public LocalDate getFechaEntregaExacta() {
		return fechaEntregaExacta;
	}
	public void setFechaEntregaExacta(LocalDate fechaEntregaExacta) {
		this.fechaEntregaExacta = fechaEntregaExacta;
	}
	public LocalDate getFechaEntregaHasta() {
		return fechaEntregaHasta;
	}
	public void setFechaEntregaHasta(LocalDate fechaEntregaHasta) {
		this.fechaEntregaHasta = fechaEntregaHasta;
	}
}
