package com.tugestor.gestortareas.dto;

import java.time.LocalDateTime;

import com.tugestor.gestortareas.model.RecordatorioTarea;
import com.tugestor.gestortareas.model.TipoRecordatorioTarea;

public class RecordatorioTareaResponse {
	private Long idRecordatorioTarea;
	private Long idTarea;
	private TipoRecordatorioTarea tipo;
	private boolean activo;
	private LocalDateTime fechaProgramada;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;
	private LocalDateTime fechaProcesado;
	private boolean notificacionGenerada;

	public RecordatorioTareaResponse() {
	}
	public RecordatorioTareaResponse(RecordatorioTarea recordatorio) {
		this.idRecordatorioTarea = recordatorio.getIdRecordatorioTarea();
		if (recordatorio.getTarea() != null) {
			this.idTarea = recordatorio.getTarea().getIdTarea();
		}
		this.tipo = recordatorio.getTipo();
		this.activo = recordatorio.isActivo();
		this.fechaProgramada = recordatorio.getFechaProgramada();
		this.fechaCreacion = recordatorio.getFechaCreacion();
		this.fechaActualizacion = recordatorio.getFechaActualizacion();
		this.fechaProcesado = recordatorio.getFechaProcesado();
		this.notificacionGenerada = recordatorio.isNotificacionGenerada();
	}
	public RecordatorioTareaResponse(Long idTarea, TipoRecordatorioTarea tipo, boolean activo) {
		this.idTarea = idTarea;
		this.tipo = tipo;
		this.activo = activo;
	}

	public Long getIdRecordatorioTarea() {
		return idRecordatorioTarea;
	}
	public void setIdRecordatorioTarea(Long idRecordatorioTarea) {
		this.idRecordatorioTarea = idRecordatorioTarea;
	}
	public Long getIdTarea() {
		return idTarea;
	}
	public void setIdTarea(Long idTarea) {
		this.idTarea = idTarea;
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
