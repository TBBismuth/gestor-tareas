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

@Entity
public class Notificacion {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idNotificacion;
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	@ManyToOne
	@JoinColumn(name = "tarea_id")
	private Tarea tarea;
	@ManyToOne
	@JoinColumn(name = "grupo_id")
	private Grupo grupo;
	@ManyToOne
	@JoinColumn(name = "asignacion_grupo_miembro_id")
	private AsignacionGrupoMiembro asignacionGrupoMiembro;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoNotificacion tipo;
	@Column(nullable = false, length = 150)
	private String titulo;
	@Column(nullable = false, length = 1000)
	private String mensaje;
	private LocalDateTime fechaProgramada;
	private LocalDateTime fechaCreacion = LocalDateTime.now();
	@Column(nullable = false)
	private boolean cerrada;
	private LocalDateTime fechaCierre;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoPushNotificacion pushEstado = EstadoPushNotificacion.PENDIENTE;
	private LocalDateTime fechaEnvioPush;
	@Column(length = 1000)
	private String errorPush;

	public Notificacion() {
	}

	public Long getIdNotificacion() {
		return idNotificacion;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Tarea getTarea() {
		return tarea;
	}
	public void setTarea(Tarea tarea) {
		this.tarea = tarea;
	}
	public Grupo getGrupo() {
		return grupo;
	}
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
	public AsignacionGrupoMiembro getAsignacionGrupoMiembro() {
		return asignacionGrupoMiembro;
	}
	public void setAsignacionGrupoMiembro(AsignacionGrupoMiembro asignacionGrupoMiembro) {
		this.asignacionGrupoMiembro = asignacionGrupoMiembro;
	}
	public TipoNotificacion getTipo() {
		return tipo;
	}
	public void setTipo(TipoNotificacion tipo) {
		this.tipo = tipo;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public LocalDateTime getFechaProgramada() {
		return fechaProgramada;
	}
	public void setFechaProgramada(LocalDateTime fechaProgramada) {
		this.fechaProgramada = fechaProgramada;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public boolean isCerrada() {
		return cerrada;
	}
	public void setCerrada(boolean cerrada) {
		this.cerrada = cerrada;
	}
	public LocalDateTime getFechaCierre() {
		return fechaCierre;
	}
	public void setFechaCierre(LocalDateTime fechaCierre) {
		this.fechaCierre = fechaCierre;
	}
	public EstadoPushNotificacion getPushEstado() {
		return pushEstado;
	}
	public void setPushEstado(EstadoPushNotificacion pushEstado) {
		this.pushEstado = pushEstado;
	}
	public LocalDateTime getFechaEnvioPush() {
		return fechaEnvioPush;
	}
	public void setFechaEnvioPush(LocalDateTime fechaEnvioPush) {
		this.fechaEnvioPush = fechaEnvioPush;
	}
	public String getErrorPush() {
		return errorPush;
	}
	public void setErrorPush(String errorPush) {
		this.errorPush = errorPush;
	}
}
