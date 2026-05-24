package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.Grupo;
import com.tugestor.gestortareas.model.RolGrupo;

public class GrupoResponse {
	private Long idGrupo;
	private String codigoPublico;
	private String nombre;
	private String descripcion;
	private String icono;
	private String color;
	private boolean activo;
	private LocalDateTime fechaCreacion;
	private Long idCreador;
	private String nombreCreador;
	private RolGrupo rolUsuarioActual;
	private boolean creadorActual;

	public GrupoResponse() {
	}
	public GrupoResponse(Grupo grupo) {
		this.idGrupo = grupo.getIdGrupo();
		this.codigoPublico = grupo.getCodigoPublico();
		this.nombre = grupo.getNombre();
		this.descripcion = grupo.getDescripcion();
		this.icono = grupo.getIcono();
		this.color = grupo.getColor();
		this.activo = grupo.isActivo();
		this.fechaCreacion = grupo.getFechaCreacion();
		if (grupo.getCreador() != null) {
			this.idCreador = grupo.getCreador().getIdUsuario();
			this.nombreCreador = grupo.getCreador().getNombre();
		}
	}
	public GrupoResponse(Grupo grupo, RolGrupo rolUsuarioActual, boolean creadorActual) {
		this(grupo);
		this.rolUsuarioActual = rolUsuarioActual;
		this.creadorActual = creadorActual;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getCodigoPublico() {
		return codigoPublico;
	}
	public void setCodigoPublico(String codigoPublico) {
		this.codigoPublico = codigoPublico;
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
	public Long getIdCreador() {
		return idCreador;
	}
	public void setIdCreador(Long idCreador) {
		this.idCreador = idCreador;
	}
	public String getNombreCreador() {
		return nombreCreador;
	}
	public void setNombreCreador(String nombreCreador) {
		this.nombreCreador = nombreCreador;
	}
	public RolGrupo getRolUsuarioActual() {
		return rolUsuarioActual;
	}
	public void setRolUsuarioActual(RolGrupo rolUsuarioActual) {
		this.rolUsuarioActual = rolUsuarioActual;
	}
	public boolean isCreadorActual() {
		return creadorActual;
	}
	public void setCreadorActual(boolean creadorActual) {
		this.creadorActual = creadorActual;
	}
}
