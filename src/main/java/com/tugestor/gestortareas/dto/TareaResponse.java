package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

public class TareaResponse {

	private Long idTarea;
	private String titulo;
	private String descripcion;
	private int tiempo;
	private Prioridad prioridad;
	private LocalDateTime fechaEntrega;
	private LocalDateTime fechaAgregado;
	private boolean completada;
	private LocalDateTime fechaCompletada;
	private String categoriaNombre;
	private Long idCategoria;
	private Long idUsuario;
	private String estado;

	public TareaResponse() {
	}
	public TareaResponse(Long idTarea, String titulo, String descripcion, int tiempo, Prioridad prioridad,
			LocalDateTime fechaEntrega, LocalDateTime fechaAgregado, boolean completada,
			LocalDateTime fechaCompletada, String categoriaNombre, Long idCategoria,
			Long idUsuario, String estado) {
		this.idTarea = idTarea;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.tiempo = tiempo;
		this.prioridad = prioridad;
		this.fechaEntrega = fechaEntrega;
		this.fechaAgregado = fechaAgregado;
		this.completada = completada;
		this.fechaCompletada = fechaCompletada;
		this.categoriaNombre = categoriaNombre;
		this.idCategoria = idCategoria;
		this.idUsuario = idUsuario;
		this.estado = estado;
	}
	public TareaResponse(Tarea tarea) {
		this.idTarea = tarea.getIdTarea();
		this.titulo = tarea.getTitulo();
		this.descripcion = tarea.getDescripcion();
		this.tiempo = tarea.getTiempo();
		this.prioridad = tarea.getPrioridad();
		this.fechaEntrega = tarea.getFechaEntrega();
		this.fechaAgregado = tarea.getFechaAgregado();
		this.completada = tarea.isCompletada();
		this.fechaCompletada = tarea.getFechaCompletada();
		this.idCategoria = tarea.getCategoria() != null ? tarea.getCategoria().getIdCategoria() : null;
		this.categoriaNombre = tarea.getCategoria() != null ? tarea.getCategoria().getNombre() : null;
		this.idUsuario = tarea.getUsuario() != null ? tarea.getUsuario().getIdUsuario() : null;
		this.estado = tarea.getEstado().name();
	}


	public Long getIdTarea() {
		return idTarea;
	}
	public void setIdTarea(Long idTarea) {
		this.idTarea = idTarea;
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
	public LocalDateTime getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(LocalDateTime fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public LocalDateTime getFechaAgregado() {
		return fechaAgregado;
	}
	public void setFechaAgregado(LocalDateTime fechaAgregado) {
		this.fechaAgregado = fechaAgregado;
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
	public String getCategoriaNombre() {
		return categoriaNombre;
	}
	public void setCategoriaNombre(String categoriaNombre) {
		this.categoriaNombre = categoriaNombre;
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
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
}
