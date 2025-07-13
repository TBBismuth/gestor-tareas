package com.tugestor.gestortareas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Categoria {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idCategoria;
	@NotBlank(message = "El nombre de la categoría no puede estar vacío")
	@Size(min = 3, max = 32, message = "El nombre de la categoría debe tener entre 3 y 32 caracteres")
	private String nombre;
	private String color; // Formate hex #RRGGBB, usado pra el frontend
	private String icono; // Nombre del icono para librerias visuales en React Native
	@Column(nullable = false)
	private boolean protegida;

	
	public Categoria() {
		// Constructor por defecto para JPA
	}

	public Categoria(String nombre, String color, String icono, boolean protegida) {
		this.nombre = nombre;
		this.color = color;
		this.icono = icono;
		this.protegida = protegida;
	}
	
	public Long getIdCategoria() {
		return idCategoria;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getIcono() {
		return icono;
	}
	public void setIcono(String icono) {
		this.icono = icono;
	}
	public boolean isProtegida() {
		return protegida;
	}
	public void setProtegida(boolean protegida) {
		this.protegida = protegida;
	}
	
	
}
