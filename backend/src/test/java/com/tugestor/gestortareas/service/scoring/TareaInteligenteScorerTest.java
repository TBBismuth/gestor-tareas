package com.tugestor.gestortareas.service.scoring;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

class TareaInteligenteScorerTest {
	private final TareaInteligenteScorer scorer = new TareaInteligenteScorer();
	private final LocalDateTime ahora = LocalDateTime.of(2026, 5, 21, 10, 0);

	@Test
	void tareaFuturaConPocoMargenRealSuperaAltaLejana() {
		Tarea proxima = tarea(1L, Prioridad.MEDIA, 120, ahora.plusHours(2));
		Tarea altaLejana = tarea(2L, Prioridad.ALTA, 30, ahora.plusDays(8));

		double scoreProxima = scorer.calcularScore(TareaScoringContext.personal(proxima), ahora);
		double scoreAltaLejana = scorer.calcularScore(TareaScoringContext.personal(altaLejana), ahora);

		assertTrue(scoreProxima > scoreAltaLejana);
	}

	@Test
	void tareaVencidaRecienteYCortaSuperaVencidaAntigua() {
		Tarea reciente = tarea(1L, Prioridad.MEDIA, 15, ahora.minusHours(3));
		Tarea antigua = tarea(2L, Prioridad.MEDIA, 15, ahora.minusDays(35));

		double scoreReciente = scorer.calcularScore(TareaScoringContext.personal(reciente), ahora);
		double scoreAntigua = scorer.calcularScore(TareaScoringContext.personal(antigua), ahora);

		assertTrue(scoreReciente > scoreAntigua);
	}

	@Test
	void tareaSinFechaImprescindibleYCortaRecibeBonusSinFecha() {
		Tarea sinFecha = tarea(1L, Prioridad.IMPRESCINDIBLE, 15, null);
		Tarea bajaSinFecha = tarea(2L, Prioridad.BAJA, 15, null);

		double scoreSinFecha = scorer.calcularScore(TareaScoringContext.personal(sinFecha), ahora);
		double scoreBajaSinFecha = scorer.calcularScore(TareaScoringContext.personal(bajaSinFecha), ahora);

		assertTrue(scoreSinFecha > scoreBajaSinFecha);
		assertTrue(scoreSinFecha >= 12);
	}

	@Test
	void tareaReabiertaProximaAplicaMultiplicadorRevisionGrupo() {
		Tarea tarea = tarea(1L, Prioridad.ALTA, 30, ahora.plusHours(24));
		AsignacionGrupoMiembro reabierta = asignacion(tarea, EstadoRevisionAsignacion.REABIERTA);
		AsignacionGrupoMiembro pendiente = asignacion(tarea, EstadoRevisionAsignacion.PENDIENTE);

		double scoreReabierta = scorer.calcularScore(TareaScoringContext.grupo(reabierta), ahora);
		double scorePendiente = scorer.calcularScore(TareaScoringContext.grupo(pendiente), ahora);

		assertTrue(scoreReabierta > scorePendiente);
	}

	@Test
	void tareaReabiertaLejanaNoAplicaMultiplicadorRevisionGrupo() {
		Tarea tarea = tarea(1L, Prioridad.ALTA, 30, ahora.plusDays(10));
		AsignacionGrupoMiembro reabierta = asignacion(tarea, EstadoRevisionAsignacion.REABIERTA);
		AsignacionGrupoMiembro pendiente = asignacion(tarea, EstadoRevisionAsignacion.PENDIENTE);

		double scoreReabierta = scorer.calcularScore(TareaScoringContext.grupo(reabierta), ahora);
		double scorePendiente = scorer.calcularScore(TareaScoringContext.grupo(pendiente), ahora);

		assertTrue(Math.abs(scoreReabierta - scorePendiente) < 0.0001);
	}

	private Tarea tarea(Long id, Prioridad prioridad, int tiempo, LocalDateTime fechaEntrega) {
		Tarea tarea = new Tarea(id);
		tarea.setTitulo("Tarea " + id);
		tarea.setPrioridad(prioridad);
		tarea.setTiempo(tiempo);
		tarea.setFechaEntrega(fechaEntrega);
		tarea.setFechaAgregado(ahora.minusDays(2));
		tarea.setCompletada(false);
		return tarea;
	}

	private AsignacionGrupoMiembro asignacion(Tarea tarea, EstadoRevisionAsignacion estadoRevision) {
		AsignacionGrupoMiembro asignacion = new AsignacionGrupoMiembro();
		asignacion.setTareaGenerada(tarea);
		asignacion.setEstadoRevision(estadoRevision);
		return asignacion;
	}
}
