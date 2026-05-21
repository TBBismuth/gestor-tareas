package com.tugestor.gestortareas.service.scoring;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

@Service
public class TareaInteligenteRankingService {
	private final Clock clock;
	private final TareaInteligenteScorer scorer;
	
	public TareaInteligenteRankingService(Clock clock) {
		this.clock = clock;
		this.scorer = new TareaInteligenteScorer();
	}
	
	public boolean esCandidata(TareaScoringContext context) {
		if (context == null || context.getTarea() == null) {
			return false;
		}
		Tarea tarea = context.getTarea();
		Estado estado = calcularEstado(tarea, ahora());
		if (estado == Estado.COMPLETADA || estado == Estado.COMPLETADA_CON_RETRASO) {
			return false;
		}
		return !context.estaEntregada() && !context.estaValidada();
	}
	
	public double calcularScore(TareaScoringContext context) {
		return scorer.calcularScore(context, ahora());
	}
	
	public List<TareaScoringContext> ordenarPorScoreDesc(List<TareaScoringContext> contextos) {
		if (contextos == null) {
			return List.of();
		}
		return contextos.stream()
				.filter(this::esCandidata)
				.sorted(comparadorInteligente())
				.toList();
	}
	
	public Comparator<TareaScoringContext> comparadorInteligente() {
		LocalDateTime ahora = ahora();
		Comparator<TareaScoringContext> porScore = Comparator
				.comparingDouble((TareaScoringContext context) -> scorer.calcularScore(context, ahora))
				.reversed();
		Comparator<TareaScoringContext> porFechaEntrega = Comparator
				.comparing((TareaScoringContext context) -> fechaEntrega(context),
						Comparator.nullsLast(LocalDateTime::compareTo));
		Comparator<TareaScoringContext> porPrioridad = Comparator
				.comparingInt((TareaScoringContext context) -> prioridadOrden(context))
				.reversed();
		Comparator<TareaScoringContext> porId = Comparator
				.comparing((TareaScoringContext context) -> idTarea(context),
						Comparator.nullsLast(Long::compareTo));
		return porScore.thenComparing(porFechaEntrega).thenComparing(porPrioridad).thenComparing(porId);
	}
	
	private LocalDateTime ahora() {
		return LocalDateTime.now(clock);
	}
	
	private Estado calcularEstado(Tarea tarea, LocalDateTime ahora) {
		if (tarea.getFechaEntrega() == null) {
			return tarea.isCompletada() ? Estado.COMPLETADA : Estado.SIN_FECHA;
		}
		if (tarea.isCompletada()) {
			return tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isAfter(tarea.getFechaEntrega())
					? Estado.COMPLETADA_CON_RETRASO
					: Estado.COMPLETADA;
		}
		return ahora.isAfter(tarea.getFechaEntrega()) ? Estado.VENCIDA : Estado.EN_CURSO;
	}
	
	private LocalDateTime fechaEntrega(TareaScoringContext context) {
		return context.getTarea() != null ? context.getTarea().getFechaEntrega() : null;
	}
	
	private int prioridadOrden(TareaScoringContext context) {
		Prioridad prioridad = context.getTarea() != null ? context.getTarea().getPrioridad() : null;
		return prioridad != null ? prioridad.ordinal() : Prioridad.BAJA.ordinal();
	}
	
	private Long idTarea(TareaScoringContext context) {
		return context.getTarea() != null ? context.getTarea().getIdTarea() : null;
	}
}
