package com.tugestor.gestortareas.service.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tugestor.gestortareas.model.AsignacionGrupoMiembro;
import com.tugestor.gestortareas.model.EstadoRevisionAsignacion;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

class TareaInteligenteRankingServiceTest {
	private final LocalDateTime ahora = LocalDateTime.of(2026, 5, 21, 10, 0);
	private final TareaInteligenteRankingService rankingService =
			new TareaInteligenteRankingService(clockFijo());

	@Test
	void tareaGrupoEntregadaOValidadaQuedaFueraDeCandidatas() {
		Tarea tarea = tarea(1L, Prioridad.ALTA, 30, ahora.plusHours(2));

		assertFalse(rankingService.esCandidata(contextoGrupo(tarea, EstadoRevisionAsignacion.ENTREGADA)));
		assertFalse(rankingService.esCandidata(contextoGrupo(tarea, EstadoRevisionAsignacion.VALIDADA)));
		assertTrue(rankingService.esCandidata(contextoGrupo(tarea, EstadoRevisionAsignacion.REABIERTA)));
		assertTrue(rankingService.esCandidata(contextoGrupo(tarea, EstadoRevisionAsignacion.PENDIENTE)));
	}

	@Test
	void tareaCompletadaQuedaFueraDeCandidatas() {
		Tarea tarea = tarea(1L, Prioridad.ALTA, 30, ahora.plusHours(2));
		tarea.setCompletada(true);
		tarea.setFechaCompletada(ahora);

		assertFalse(rankingService.esCandidata(TareaScoringContext.personal(tarea)));
	}

	@Test
	void ordenarPorScoreDescFiltraNoCandidatas() {
		Tarea urgente = tarea(1L, Prioridad.MEDIA, 90, ahora.plusHours(1));
		Tarea lejana = tarea(2L, Prioridad.ALTA, 30, ahora.plusDays(8));
		Tarea completada = tarea(3L, Prioridad.IMPRESCINDIBLE, 10, ahora.plusHours(1));
		completada.setCompletada(true);
		completada.setFechaCompletada(ahora);

		List<TareaScoringContext> resultado = rankingService.ordenarPorScoreDesc(List.of(
				TareaScoringContext.personal(lejana),
				TareaScoringContext.personal(completada),
				TareaScoringContext.personal(urgente)));

		assertEquals(2, resultado.size());
		assertEquals(urgente.getIdTarea(), resultado.get(0).getTarea().getIdTarea());
	}

	private TareaScoringContext contextoGrupo(Tarea tarea, EstadoRevisionAsignacion estadoRevision) {
		AsignacionGrupoMiembro asignacion = new AsignacionGrupoMiembro();
		asignacion.setTareaGenerada(tarea);
		asignacion.setEstadoRevision(estadoRevision);
		return TareaScoringContext.grupo(asignacion);
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

	private Clock clockFijo() {
		ZoneId zoneId = ZoneId.systemDefault();
		return Clock.fixed(ahora.atZone(zoneId).toInstant(), zoneId);
	}
}
