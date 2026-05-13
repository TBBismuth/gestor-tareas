package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.TipoAsignacionGrupo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AsignacionGrupoRequest {
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
	@NotNull(message = "El tipo de asignacion no puede ser nulo")
	@Enumerated(EnumType.STRING)
	private TipoAsignacionGrupo tipoAsignacion;
	private List<Long> idsUsuarios;

	public AsignacionGrupoRequest() {
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
	public TipoAsignacionGrupo getTipoAsignacion() {
		return tipoAsignacion;
	}
	public void setTipoAsignacion(TipoAsignacionGrupo tipoAsignacion) {
		this.tipoAsignacion = tipoAsignacion;
	}
	public List<Long> getIdsUsuarios() {
		return idsUsuarios;
	}
	public void setIdsUsuarios(List<Long> idsUsuarios) {
		this.idsUsuarios = idsUsuarios;
	}
}
