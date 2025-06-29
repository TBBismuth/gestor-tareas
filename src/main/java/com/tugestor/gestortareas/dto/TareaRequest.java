package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.Prioridad;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TareaRequest {
	@NotBlank(message = "El titulo no puede estar vacio")
	@Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
	private String titulo;
	@Min(value = 1, message = "El tiempo debe ser mayor a 0")
	private int tiempo;
	@NotNull(message = "La prioridad no puede ser nula")
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	@Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
	private String descripcion;
	@NotNull(message = "La fecha de entrega no puede ser nula")
	@FutureOrPresent(message = "La fecha de entrega no puede haber pasado")
	private LocalDateTime fechaEntrega;
	private Long idCategoria;
	@NotNull(message = "El ID del usuario no puede ser nulo")
	private Long idUsuario;
	private boolean completada;
	private LocalDateTime fechaCompletada;

	
	public TareaRequest() {
	}

	public TareaRequest(String titulo, int tiempo, Prioridad prioridad, LocalDateTime fechaEntrega, String descripcion,
			Long idCategoria, Long idUsuario) {
		this.titulo = titulo;
		this.tiempo = tiempo;
		this.prioridad = prioridad;
		this.fechaEntrega = fechaEntrega;
		this.descripcion = descripcion;
		this.idCategoria = idCategoria;
		this.idUsuario = idUsuario;
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getTiempo() {
		return tiempo;
	}
	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(LocalDateTime fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public Long getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}
	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public boolean isCompletada() {
		return completada;
	}

	public void setCompletada(boolean completada) {
		this.completada = completada;
	}
	public LocalDateTime getFechaCompletada() {
		return fechaCompletada;
	}
	public void setFechaCompletada(LocalDateTime fechaCompletada) {
		this.fechaCompletada = fechaCompletada;
	}
}
