package com.tugestor.gestortareas.service;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.RecordatorioInteligenteRequest;
import com.tugestor.gestortareas.dto.RecordatorioTareaResponse;
import com.tugestor.gestortareas.model.RecordatorioTarea;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.TipoRecordatorioTarea;
import com.tugestor.gestortareas.repository.RecordatorioTareaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

@Service
public class RecordatorioTareaServiceImpl implements RecordatorioTareaService {
	private final RecordatorioTareaRepository rtr;
	private final TareaRepository tr;

	public RecordatorioTareaServiceImpl(RecordatorioTareaRepository rtr, TareaRepository tr) {
		this.rtr = rtr;
		this.tr = tr;
	}

	@Override
	@Transactional
	public RecordatorioTareaResponse configurarRecordatorioInteligente(Long idTarea,
			RecordatorioInteligenteRequest request, String emailUsuario) {
		Tarea tarea = tr.findById(idTarea)
				.orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con id: " + idTarea));
		validarPropietario(tarea, emailUsuario);
		return Boolean.TRUE.equals(request.getActivo())
				? activarRecordatorioInteligente(tarea)
				: desactivarRecordatorioInteligente(tarea);
	}

	private RecordatorioTareaResponse activarRecordatorioInteligente(Tarea tarea) {
		if (tarea.getFechaEntrega() == null) {
			throw new ValidationException("La tarea debe tener fecha de entrega para activar el recordatorio inteligente.");
		}
		LocalDateTime ahora = LocalDateTime.now();
		LocalDateTime fechaProgramada = tarea.getFechaEntrega()
				.minusHours(1)
				.minusMinutes(tarea.getTiempo());
		if (!fechaProgramada.isAfter(ahora)) {
			throw new ValidationException("La fecha calculada del recordatorio inteligente ya ha vencido.");
		}

		RecordatorioTarea recordatorio = rtr.findByUsuarioAndTareaAndTipo(
				tarea.getUsuario(), tarea, TipoRecordatorioTarea.RECORDATORIO_INTELIGENTE)
				.orElseGet(RecordatorioTarea::new);
		recordatorio.setUsuario(tarea.getUsuario());
		recordatorio.setTarea(tarea);
		recordatorio.setTipo(TipoRecordatorioTarea.RECORDATORIO_INTELIGENTE);
		recordatorio.setActivo(true);
		recordatorio.setFechaProgramada(fechaProgramada);
		recordatorio.setFechaActualizacion(ahora);
		recordatorio.setFechaProcesado(null);
		recordatorio.setNotificacionGenerada(false);
		return new RecordatorioTareaResponse(rtr.save(recordatorio));
	}

	private RecordatorioTareaResponse desactivarRecordatorioInteligente(Tarea tarea) {
		return rtr.findByUsuarioAndTareaAndTipo(
				tarea.getUsuario(), tarea, TipoRecordatorioTarea.RECORDATORIO_INTELIGENTE)
				.map(recordatorio -> {
					recordatorio.setActivo(false);
					recordatorio.setFechaActualizacion(LocalDateTime.now());
					return new RecordatorioTareaResponse(rtr.save(recordatorio));
				})
				.orElseGet(() -> new RecordatorioTareaResponse(
						tarea.getIdTarea(), TipoRecordatorioTarea.RECORDATORIO_INTELIGENTE, false));
	}

	private void validarPropietario(Tarea tarea, String emailUsuario) {
		if (tarea.getUsuario() == null || !tarea.getUsuario().getEmail().equals(emailUsuario)) {
			throw new AccessDeniedException("No puedes modificar recordatorios de tareas que no son tuyas.");
		}
	}
}
