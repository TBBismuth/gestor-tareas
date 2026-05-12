package com.tugestor.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GrupoRequest {
	@NotBlank(message = "El nombre del grupo no puede estar vacio")
	@Size(min = 3, max = 64, message = "El nombre del grupo debe tener entre 3 y 64 caracteres")
	private String nombre;
	@Size(max = 500, message = "La descripcion del grupo no puede exceder los 500 caracteres")
	private String descripcion;
	private String icono;
	private String color;

	public GrupoRequest() {
	}
	public GrupoRequest(String nombre, String descripcion, String icono, String color) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.icono = icono;
		this.color = color;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getIcono() {
		return icono;
	}
	public void setIcono(String icono) {
		this.icono = icono;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
