package com.tugestor.gestortareas.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.CriterioOrdenTareaCombinado;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.EstadoFiltroTarea;
import com.tugestor.gestortareas.model.OrigenTareaFiltro;
import com.tugestor.gestortareas.model.Prioridad;

public class EstadoFiltroTareaResponse {
	private OrigenTareaFiltro origen;
	private Long idGrupo;
	private Prioridad prioridad;
	private Estado estado;
	private String palabrasClave;
	private Integer tiempoMax;
	private Long idCategoria;
	private LocalDate fechaEntregaExacta;
	private LocalDate fechaEntregaHasta;
	private CriterioOrdenTareaCombinado criterioOrdenActivo;
	private Boolean soloPorCompletar;
	private LocalDateTime updatedAt;

	public EstadoFiltroTareaResponse() {
	}

	public EstadoFiltroTareaResponse(EstadoFiltroTarea estadoFiltro) {
		this.origen = estadoFiltro.getOrigen();
		this.idGrupo = estadoFiltro.getIdGrupo();
		this.prioridad = estadoFiltro.getPrioridad();
		this.estado = estadoFiltro.getEstado();
		this.palabrasClave = estadoFiltro.getPalabrasClave();
		this.tiempoMax = estadoFiltro.getTiempoMax();
		this.idCategoria = estadoFiltro.getIdCategoria();
		this.fechaEntregaExacta = estadoFiltro.getFechaEntregaExacta();
		this.fechaEntregaHasta = estadoFiltro.getFechaEntregaHasta();
		this.criterioOrdenActivo = estadoFiltro.getCriterioOrdenActivo();
		this.soloPorCompletar = estadoFiltro.getSoloPorCompletar();
		this.updatedAt = estadoFiltro.getUpdatedAt();
	}

	public static EstadoFiltroTareaResponse porDefecto() {
		EstadoFiltroTareaResponse response = new EstadoFiltroTareaResponse();
		response.origen = OrigenTareaFiltro.TODAS;
		response.criterioOrdenActivo = CriterioOrdenTareaCombinado.FECHA_AGREGADO;
		response.soloPorCompletar = true;
		return response;
	}

	public OrigenTareaFiltro getOrigen() {
		return origen;
	}
	public Long getIdGrupo() {
		return idGrupo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public Estado getEstado() {
		return estado;
	}
	public String getPalabrasClave() {
		return palabrasClave;
	}
	public Integer getTiempoMax() {
		return tiempoMax;
	}
	public Long getIdCategoria() {
		return idCategoria;
	}
	public LocalDate getFechaEntregaExacta() {
		return fechaEntregaExacta;
	}
	public LocalDate getFechaEntregaHasta() {
		return fechaEntregaHasta;
	}
	public CriterioOrdenTareaCombinado getCriterioOrdenActivo() {
		return criterioOrdenActivo;
	}
	public Boolean getSoloPorCompletar() {
		return soloPorCompletar;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
