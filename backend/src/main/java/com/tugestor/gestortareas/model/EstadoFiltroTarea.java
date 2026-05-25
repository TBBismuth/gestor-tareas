package com.tugestor.gestortareas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class EstadoFiltroTarea {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idEstadoFiltroTarea;
	@OneToOne
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private Usuario usuario;
	@Enumerated(EnumType.STRING)
	private OrigenTareaFiltro origen = OrigenTareaFiltro.TODAS;
	private Long idGrupo;
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	@ElementCollection
	@CollectionTable(name = "estado_filtro_tarea_prioridades", joinColumns = @JoinColumn(name = "estado_filtro_tarea_id"))
	@Column(name = "prioridad")
	@Enumerated(EnumType.STRING)
	private List<Prioridad> prioridades = new ArrayList<>();
	@Enumerated(EnumType.STRING)
	private Estado estado;
	@ElementCollection
	@CollectionTable(name = "estado_filtro_tarea_estados", joinColumns = @JoinColumn(name = "estado_filtro_tarea_id"))
	@Column(name = "estado")
	@Enumerated(EnumType.STRING)
	private List<Estado> estados = new ArrayList<>();
	private String palabrasClave;
	private Integer tiempoMax;
	private Long idCategoria;
	private LocalDate fechaEntregaExacta;
	private LocalDate fechaEntregaHasta;
	@Enumerated(EnumType.STRING)
	private CriterioOrdenTareaCombinado criterioOrdenActivo = CriterioOrdenTareaCombinado.FECHA_AGREGADO;
	private Boolean soloPorCompletar = true;
	private LocalDateTime updatedAt = LocalDateTime.now();

	public EstadoFiltroTarea() {
		// Constructor por defecto para JPA
	}

	public Long getIdEstadoFiltroTarea() {
		return idEstadoFiltroTarea;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	public List<Prioridad> getPrioridades() {
		return prioridades;
	}
	public void setPrioridades(List<Prioridad> prioridades) {
		this.prioridades = prioridades != null ? new ArrayList<>(prioridades) : new ArrayList<>();
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public List<Estado> getEstados() {
		return estados;
	}
	public void setEstados(List<Estado> estados) {
		this.estados = estados != null ? new ArrayList<>(estados) : new ArrayList<>();
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
	public CriterioOrdenTareaCombinado getCriterioOrdenActivo() {
		return criterioOrdenActivo;
	}
	public void setCriterioOrdenActivo(CriterioOrdenTareaCombinado criterioOrdenActivo) {
		this.criterioOrdenActivo = criterioOrdenActivo;
	}
	public Boolean getSoloPorCompletar() {
		return soloPorCompletar;
	}
	public void setSoloPorCompletar(Boolean soloPorCompletar) {
		this.soloPorCompletar = soloPorCompletar;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
