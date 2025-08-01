package com.tugestor.gestortareas.dto;

import com.tugestor.gestortareas.model.Categoria;

public class CategoriaResponse {
	private Long idCategoria;
	private String nombre;
	private String color;
	private String icono;
	private boolean protegida;	//Aunque no lo uso en el DTO de entrada, me será util en el frontend para mostrar o no boton eliminar

	public CategoriaResponse() {
	}
	public CategoriaResponse(Long idCategoria, String nombre, String color, String icono, boolean protegida) {
		this.idCategoria = idCategoria;
		this.nombre = nombre;
		this.color = color;
		this.icono = icono;
		this.protegida = protegida;
	}
	public CategoriaResponse(Categoria categoria) {
		this.idCategoria = categoria.getIdCategoria();
		this.nombre = categoria.getNombre();
		this.color = categoria.getColor();
		this.icono = categoria.getIcono();
		this.protegida = categoria.isProtegida();
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
	public boolean isProtegida() {
		return protegida;
	}
	public void setProtegida(boolean protegida) {
		this.protegida = protegida;
	}

}
