package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.Categoria;

public class CategoriaResponse {
	private Long idCategoria;
	private String nombre;
	private String color;
	private String icono;

	public CategoriaResponse() {
	}
	public CategoriaResponse(Long idCategoria, String nombre, String color, String icono) {
		this.idCategoria = idCategoria;
		this.nombre = nombre;
		this.color = color;
		this.icono = icono;
	}
	public CategoriaResponse(Categoria categoria) {
		this.idCategoria = categoria.getIdCategoria();
		this.nombre = categoria.getNombre();
		this.color = categoria.getColor();
		this.icono = categoria.getIcono();
	}

	public Long getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
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
