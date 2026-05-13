package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class AsignacionGrupo {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idAsignacionGrupo;
	@ManyToOne
	@JoinColumn(name = "grupo_id")
	private Grupo grupo;
	@ManyToOne
	@JoinColumn(name = "creador_asignacion_id", nullable = false)
	private Usuario creadorAsignacion;
	@NotBlank(message = "El titulo no puede estar vacio")
	@Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
	private String titulo;
	@Size(max = 1000, message = "La descripcion no puede exceder los 1000 caracteres")
	private String descripcion;
	@NotNull(message = "La prioridad no puede ser nula")
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	@Min(value = 1, message = "El tiempo debe ser mayor a 0")
	private int tiempo;
	private LocalDateTime fechaEntrega;
	private LocalDateTime fechaCreacion = LocalDateTime.now();
	@NotNull(message = "El tipo de asignacion no puede ser nulo")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoAsignacionGrupo tipoAsignacion;

	public AsignacionGrupo() {
		// Constructor por defecto para JPA
	}

	public Long getIdAsignacionGrupo() {
		return idAsignacionGrupo;
	}
	public Grupo getGrupo() {
		return grupo;
	}
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
	public Usuario getCreadorAsignacion() {
		return creadorAsignacion;
	}
	public void setCreadorAsignacion(Usuario creadorAsignacion) {
		this.creadorAsignacion = creadorAsignacion;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}
	public int getTiempo() {
		return tiempo;
	}
	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(LocalDateTime fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public TipoAsignacionGrupo getTipoAsignacion() {
		return tipoAsignacion;
	}
	public void setTipoAsignacion(TipoAsignacionGrupo tipoAsignacion) {
		this.tipoAsignacion = tipoAsignacion;
	}
}
