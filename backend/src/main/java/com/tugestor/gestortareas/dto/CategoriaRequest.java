package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaRequest {
	@NotBlank(message = "El nombre de la categoría no puede estar vacío")
	@Size(min = 3, max = 32, message = "El nombre de la categoría debe tener entre 3 y 32 caracteres")
	private String nombre;
	private String color;
	private String icono;

	public CategoriaRequest() {
	}
	public CategoriaRequest(String nombre, String color, String icono) {
		this.nombre = nombre;
		this.color = color;
		this.icono = icono;
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

}
