package com.tugestor.gestortareas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity	// Anotación de JPA para indicar que esta clase es una entidad
public class Tarea {
	@Id // Anotación de JPA para indicar que este campo es la clave primaria
	@GeneratedValue(strategy= GenerationType.IDENTITY) 
	/* Generación automática del ID
	 * IDENTITY usa AUTO_INCREMENT en la BBDD en MariaDB */
	private Long id;
	private String titulo;
	private int tiempo;
	private String prioridad;
	private LocalDateTime fechaAgregado = LocalDateTime.now();
	private LocalDate fechaEntrega;
	private String descripcion;
	
	public Tarea() {
		// Obligatorio para JPA
	}
	public Tarea(String titulo, int tiempo, String prioridad, LocalDate fechaEntrega, String descripcion) {
		this.titulo = titulo;
		this.tiempo = tiempo;
		this.prioridad = prioridad;
		this.fechaEntrega = fechaEntrega;
		this.descripcion = descripcion;
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String nombre) {
		this.titulo = nombre;
	}
	public int getTiempo() {
		return tiempo;
	}
	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	public String getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}
	public LocalDateTime getFechaAgregado() {
		return fechaAgregado;
	}
	public LocalDate getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(LocalDate fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}