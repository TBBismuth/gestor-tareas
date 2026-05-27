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
import jakarta.persistence.PreUpdate;

@Entity
public class RecordatorioTarea {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idRecordatorioTarea;
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	@ManyToOne
	@JoinColumn(name = "tarea_id", nullable = false)
	private Tarea tarea;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoRecordatorioTarea tipo;
	@Column(nullable = false)
	private boolean activo = true;
	@Column(nullable = false)
	private LocalDateTime fechaProgramada;
	private LocalDateTime fechaCreacion = LocalDateTime.now();
	private LocalDateTime fechaActualizacion = LocalDateTime.now();
	private LocalDateTime fechaProcesado;
	@Column(nullable = false)
	private boolean notificacionGenerada;

	public RecordatorioTarea() {
	}

	@PreUpdate
	public void actualizarFechaActualizacion() {
		this.fechaActualizacion = LocalDateTime.now();
	}

	public Long getIdRecordatorioTarea() {
		return idRecordatorioTarea;
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
	public TipoRecordatorioTarea getTipo() {
		return tipo;
	}
	public void setTipo(TipoRecordatorioTarea tipo) {
		this.tipo = tipo;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
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
	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public LocalDateTime getFechaProcesado() {
		return fechaProcesado;
	}
	public void setFechaProcesado(LocalDateTime fechaProcesado) {
		this.fechaProcesado = fechaProcesado;
	}
	public boolean isNotificacionGenerada() {
		return notificacionGenerada;
	}
	public void setNotificacionGenerada(boolean notificacionGenerada) {
		this.notificacionGenerada = notificacionGenerada;
	}
}
