package com.tugestor.gestortareas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Categoria {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idCategoria;
	private String nombre;
	private String color; // Formate hex #RRGGBB, usado pra el frontend
	private String icono; // Nombre del icono para librerias visuales en React Native
	
	public Categoria() {
		// Constructor por defecto para JPA
	}

	public Categoria(String nombre, String color, String icono) {
		this.nombre = nombre;
		this.color = color;
		this.icono = icono;
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
	
	
}
