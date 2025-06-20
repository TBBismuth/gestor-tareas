package com.tugestor.gestortareas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity	// Anotación de JPA para indicar que esta clase es una entidad
public class Tarea {
	@Id // Anotación de JPA para indicar que este campo es la clave primaria
	@GeneratedValue(strategy= GenerationType.IDENTITY) 
	/* Generación automática del ID
	 * IDENTITY usa AUTO_INCREMENT en la BBDD en MariaDB */
	private Long idTarea;
	private String titulo;
	private int tiempo;
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	private LocalDateTime fechaAgregado = LocalDateTime.now();
	private LocalDate fechaEntrega;
	private String descripcion;
	@ManyToOne // Relación muchos a uno con la entidad Categoria (muchas tareas -> una categoría)
	@JoinColumn(name = "id_categoria") // Nombre de la columna en la tabla Tarea que referencia a Categoria
	private Categoria categoria;
	
	public Tarea() {
		// Obligatorio para JPA
	}
	public Tarea(String titulo, int tiempo, Prioridad prioridad, LocalDate fechaEntrega, String descripcion, Categoria categoria) {
		this.titulo = titulo;
		this.tiempo = tiempo;
		this.prioridad = prioridad;
		this.fechaEntrega = fechaEntrega;
		this.descripcion = descripcion;
		this.categoria = categoria;
	}
	
	public Long getIdTarea() {
		return idTarea;
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
	public Prioridad getPrioridad() {
		return prioridad;
	}
	public void setPrioridad(Prioridad prioridad) {
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
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

}