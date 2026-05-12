package com.tugestor.gestortareas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "uk_grupo_miembro_grupo_usuario", columnNames = {"grupo_id", "usuario_id"})
})
public class GrupoMiembro {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idGrupoMiembro;
	@ManyToOne
	@JoinColumn(name = "grupo_id", nullable = false)
	private Grupo grupo;
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RolGrupo rol;
	private LocalDateTime fechaUnion = LocalDateTime.now();
	@ManyToOne
	@JoinColumn(name = "anadido_por_id")
	private Usuario anadidoPor;

	public GrupoMiembro() {
		// Constructor por defecto para JPA
	}
	public GrupoMiembro(Long idGrupoMiembro) {	// Constructor para tests
		this.idGrupoMiembro = idGrupoMiembro;
	}

	public Long getIdGrupoMiembro() {
		return idGrupoMiembro;
	}
	public Grupo getGrupo() {
		return grupo;
	}
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	public Usuario getAnadidoPor() {
		return anadidoPor;
	}
	public void setAnadidoPor(Usuario anadidoPor) {
		this.anadidoPor = anadidoPor;
	}
}
