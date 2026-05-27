package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.EstadoPushNotificacion;
import com.tugestor.gestortareas.model.Notificacion;
import com.tugestor.gestortareas.model.TipoNotificacion;

public class NotificacionResponse {
	private Long idNotificacion;
	private TipoNotificacion tipo;
	private String titulo;
	private String mensaje;
	private LocalDateTime fechaProgramada;
	private LocalDateTime fechaCreacion;
	private boolean cerrada;
	private LocalDateTime fechaCierre;
	private EstadoPushNotificacion pushEstado;
	private LocalDateTime fechaEnvioPush;
	private Long idTarea;
	private String tituloTarea;
	private Long idGrupo;
	private String nombreGrupo;
	private Long idAsignacionGrupoMiembro;

	public NotificacionResponse() {
	}
	public NotificacionResponse(Notificacion notificacion) {
		this.idNotificacion = notificacion.getIdNotificacion();
		this.tipo = notificacion.getTipo();
		this.titulo = notificacion.getTitulo();
		this.mensaje = notificacion.getMensaje();
		this.fechaProgramada = notificacion.getFechaProgramada();
		this.fechaCreacion = notificacion.getFechaCreacion();
		this.cerrada = notificacion.isCerrada();
		this.fechaCierre = notificacion.getFechaCierre();
		this.pushEstado = notificacion.getPushEstado();
		this.fechaEnvioPush = notificacion.getFechaEnvioPush();
		if (notificacion.getTarea() != null) {
			this.idTarea = notificacion.getTarea().getIdTarea();
			this.tituloTarea = notificacion.getTarea().getTitulo();
		}
		if (notificacion.getGrupo() != null) {
			this.idGrupo = notificacion.getGrupo().getIdGrupo();
			this.nombreGrupo = notificacion.getGrupo().getNombre();
		}
		if (notificacion.getAsignacionGrupoMiembro() != null) {
			this.idAsignacionGrupoMiembro = notificacion.getAsignacionGrupoMiembro()
					.getIdAsignacionGrupoMiembro();
		}
	}

	public Long getIdNotificacion() {
		return idNotificacion;
	}
	public void setIdNotificacion(Long idNotificacion) {
		this.idNotificacion = idNotificacion;
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
	public Long getIdTarea() {
		return idTarea;
	}
	public void setIdTarea(Long idTarea) {
		this.idTarea = idTarea;
	}
	public String getTituloTarea() {
		return tituloTarea;
	}
	public void setTituloTarea(String tituloTarea) {
		this.tituloTarea = tituloTarea;
	}
	public Long getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getNombreGrupo() {
		return nombreGrupo;
	}
	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}
	public Long getIdAsignacionGrupoMiembro() {
		return idAsignacionGrupoMiembro;
	}
	public void setIdAsignacionGrupoMiembro(Long idAsignacionGrupoMiembro) {
		this.idAsignacionGrupoMiembro = idAsignacionGrupoMiembro;
	}
}
