package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.GrupoMiembro;
import com.tugestor.gestortareas.model.RolGrupo;

public class GrupoMiembroResponse {
	private Long idGrupoMiembro;
	private Long idUsuario;
	private String nombre;
	private String email;
	private RolGrupo rol;
	private LocalDateTime fechaUnion;
	private Long idAnadidoPor;
	private String nombreAnadidoPor;
	private boolean creador;

	public GrupoMiembroResponse() {
	}
	public GrupoMiembroResponse(GrupoMiembro miembro) {
		this.idGrupoMiembro = miembro.getIdGrupoMiembro();
		if (miembro.getUsuario() != null) {
			this.idUsuario = miembro.getUsuario().getIdUsuario();
			this.nombre = miembro.getUsuario().getNombre();
			this.email = miembro.getUsuario().getEmail();
		}
		this.rol = miembro.getRol();
		this.fechaUnion = miembro.getFechaUnion();
		if (miembro.getAnadidoPor() != null) {
			this.idAnadidoPor = miembro.getAnadidoPor().getIdUsuario();
			this.nombreAnadidoPor = miembro.getAnadidoPor().getNombre();
		}
		if (miembro.getGrupo() != null && miembro.getGrupo().getCreador() != null && miembro.getUsuario() != null) {
			this.creador = miembro.getGrupo().getCreador().getIdUsuario().equals(miembro.getUsuario().getIdUsuario());
		}
	}

	public Long getIdGrupoMiembro() {
		return idGrupoMiembro;
	}
	public void setIdGrupoMiembro(Long idGrupoMiembro) {
		this.idGrupoMiembro = idGrupoMiembro;
	}
	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public RolGrupo getRol() {
		return rol;
	}
	public void setRol(RolGrupo rol) {
		this.rol = rol;
	}
	public LocalDateTime getFechaUnion() {
		return fechaUnion;
	}
	public void setFechaUnion(LocalDateTime fechaUnion) {
		this.fechaUnion = fechaUnion;
	}
	public Long getIdAnadidoPor() {
		return idAnadidoPor;
	}
	public void setIdAnadidoPor(Long idAnadidoPor) {
		this.idAnadidoPor = idAnadidoPor;
	}
	public String getNombreAnadidoPor() {
		return nombreAnadidoPor;
	}
	public void setNombreAnadidoPor(String nombreAnadidoPor) {
		this.nombreAnadidoPor = nombreAnadidoPor;
	}
	public boolean isCreador() {
		return creador;
	}
	public void setCreador(boolean creador) {
		this.creador = creador;
	}
}
