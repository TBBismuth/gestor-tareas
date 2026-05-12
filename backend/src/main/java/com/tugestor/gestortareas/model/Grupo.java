package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Grupo {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idGrupo;
	@Column(nullable = false, unique = true, length = 16)
	private String codigoPublico;
	@Column(nullable = false, unique = true, length = 48)
	private String codigoInvitacion;
	@NotBlank(message = "El nombre del grupo no puede estar vacio")
	@Size(min = 3, max = 64, message = "El nombre del grupo debe tener entre 3 y 64 caracteres")
	private String nombre;
	@Size(max = 500, message = "La descripcion del grupo no puede exceder los 500 caracteres")
	private String descripcion;
	private String icono;
	private String color;
	@Column(nullable = false)
	private boolean activo = true;
	private LocalDateTime fechaCreacion = LocalDateTime.now();
	@ManyToOne
	@JoinColumn(name = "creador_id", nullable = false)
	private Usuario creador;
	@OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GrupoMiembro> miembros = new ArrayList<>();

	public Grupo() {
		// Constructor por defecto para JPA
	}
	public Grupo(Long idGrupo) {	// Constructor para tests
		this.idGrupo = idGrupo;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}
	public String getCodigoPublico() {
		return codigoPublico;
	}
	public void setCodigoPublico(String codigoPublico) {
		this.codigoPublico = codigoPublico;
	}
	public String getCodigoInvitacion() {
		return codigoInvitacion;
	}
	public void setCodigoInvitacion(String codigoInvitacion) {
		this.codigoInvitacion = codigoInvitacion;
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
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public Usuario getCreador() {
		return creador;
	}
	public void setCreador(Usuario creador) {
		this.creador = creador;
	}
	public List<GrupoMiembro> getMiembros() {
		return miembros;
	}
	public void setMiembros(List<GrupoMiembro> miembros) {
		this.miembros = miembros;
	}
}
