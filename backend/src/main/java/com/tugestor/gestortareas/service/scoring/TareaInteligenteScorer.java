package com.tugestor.gestortareas.service.scoring;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Prioridad;
import com.tugestor.gestortareas.model.Tarea;

public class TareaInteligenteScorer {
	private static final double[][] FUTURE_F_POINTS = {
			{ 0, 1.00 }, { 4, 0.99 }, { 9, 0.98 }, { 13, 0.97 }, { 18, 0.96 },
			{ 24, 0.95 }, { 30, 0.90 }, { 36, 0.85 }, { 42, 0.80 }, { 48, 0.75 },
			{ 54, 0.68 }, { 60, 0.61 }, { 66, 0.54 }, { 72, 0.47 }, { 96, 0.40 },
			{ 120, 0.33 }, { 144, 0.26 }, { 168, 0.20 }, { 192, 0.10 }
	};
	private static final double[][] OVERDUE_F_POINTS = {
			{ 1, 0.90 }, { 2, 0.78 }, { 3, 0.68 }, { 7, 0.36 }, { 15, 0.08 },
			{ 20, 0.00 }, { 30, -0.05 }, { 60, -0.10 }
	};
	private static final double[][] AGE_POINTS = {
			{ 0, 0.00 }, { 1, 0.01 }, { 2, 0.03 }, { 3, 0.04 }, { 4, 0.06 },
			{ 5, 0.07 }, { 6, 0.09 }, { 7, 0.10 }, { 10, 0.14 }, { 14, 0.19 },
			{ 18, 0.24 }, { 22, 0.30 }, { 26, 0.35 }, { 30, 0.40 }, { 40, 0.33 },
			{ 50, 0.27 }, { 60, 0.20 }, { 70, 0.13 }, { 80, 0.07 }, { 90, 0.00 }
	};
	
	public double calcularScore(TareaScoringContext context, LocalDateTime ahora) {
		if (context == null || context.getTarea() == null || ahora == null) {
			return 0.0;
		}
		Tarea tarea = context.getTarea();
		Prioridad prioridad = prioridadSegura(tarea);
		int tiempoMinutos = tiempoMinutosSeguro(tarea);
		double factorFecha = factorFecha(tarea, ahora);
		double factorPrioridad = factorPrioridad(prioridad);
		double factorTiempo = factorTiempo(tiempoMinutos);
		double factorAntiguedad = factorAntiguedad(tarea, ahora);
		double factorCategoria = factorCategoria(tarea);
		double mpf = multiplicadorPrioridadFecha(prioridad, factorFecha);
		double mpt = multiplicadorPrioridadTiempo(tarea, prioridad, tiempoMinutos, factorTiempo, ahora);
		double mrg = multiplicadorRevisionGrupo(context, factorFecha, ahora);
		double bvc = bonusVentanaCritica(tarea, tiempoMinutos, ahora);
		double bsf = bonusSinFecha(tarea, prioridad, tiempoMinutos);
		
		return 100 * (factorFecha * 0.45
				+ factorPrioridad * 0.35
				+ factorTiempo * 0.12
				+ factorAntiguedad * 0.05
				+ factorCategoria * 0.03)
				* mpf * mpt * mrg + bvc + bsf;
	}
	
	private double factorFecha(Tarea tarea, LocalDateTime ahora) {
		if (tarea.getFechaEntrega() == null) {
			return 0.0;
		}
		double horas = horasEntre(ahora, tarea.getFechaEntrega());
		if (horas >= 0) {
			if (horas >= 192) {
				return 0.10;
			}
			return interpolar(FUTURE_F_POINTS, horas);
		}
		double diasVencida = Math.abs(horas) / 24.0;
		if (diasVencida <= 1) {
			return 0.90;
		}
		if (diasVencida >= 60) {
			return -0.10;
		}
		return interpolar(OVERDUE_F_POINTS, diasVencida);
	}
	
	private double factorPrioridad(Prioridad prioridad) {
		return switch (prioridad) {
		case IMPRESCINDIBLE -> 1.00;
		case ALTA -> 0.75;
		case MEDIA -> 0.40;
		case BAJA -> 0.20;
		};
	}
	
	private double factorTiempo(int minutos) {
		if (minutos == 1) return 1.15;
		if (minutos <= 5) return 1.05;
		if (minutos <= 10) return 1.00;
		if (minutos <= 15) return 0.95;
		if (minutos <= 20) return 0.88;
		if (minutos <= 30) return 0.80;
		if (minutos <= 45) return 0.68;
		if (minutos <= 60) return 0.60;
		if (minutos <= 75) return 0.54;
		if (minutos <= 90) return 0.49;
		if (minutos <= 105) return 0.46;
		if (minutos <= 120) return 0.44;
		if (minutos <= 150) return 0.36;
		if (minutos <= 180) return 0.29;
		if (minutos <= 240) return 0.22;
		return 0.16;
	}
	
	private double factorAntiguedad(Tarea tarea, LocalDateTime ahora) {
		if (tarea.getFechaAgregado() == null || tarea.getFechaAgregado().isAfter(ahora)) {
			return 0.0;
		}
		double dias = horasEntre(tarea.getFechaAgregado(), ahora) / 24.0;
		if (dias >= 90) {
			return 0.0;
		}
		return interpolar(AGE_POINTS, dias);
	}
	
	private double factorCategoria(Tarea tarea) {
		Categoria categoria = tarea.getCategoria();
		if (categoria == null || !categoria.isProtegida()) {
			return 0.0;
		}
		String nombre = normalizar(categoria.getNombre());
		return switch (nombre) {
		case "TRABAJO/ESTUDIOS" -> 1.00;
		case "DOMESTICO" -> 0.60;
		case "OCIO/PERSONAL" -> 0.30;
		default -> 0.0;
		};
	}
	
	private double multiplicadorPrioridadFecha(Prioridad prioridad, double factorFecha) {
		double urgencia = Math.max(factorFecha, 0);
		double bonusPrioridad = switch (prioridad) {
		case IMPRESCINDIBLE -> 0.35;
		case ALTA -> 0.22;
		case MEDIA -> 0.10;
		case BAJA -> 0.00;
		};
		return 1 + urgencia * bonusPrioridad;
	}
	
	private double multiplicadorPrioridadTiempo(Tarea tarea, Prioridad prioridad, int tiempoMinutos,
			double factorTiempo, LocalDateTime ahora) {
		double tMpt = Math.min(factorTiempo, 1.00);
		double costeTiempo = Math.max(0, 1 - tMpt);
		double bonusRapidez = bonusRapidez(prioridad);
		double penalizacionLarga = penalizacionLarga(prioridad);
		if (tarea.getFechaEntrega() != null && tarea.getFechaEntrega().isAfter(ahora)) {
			double margenReal = horasEntre(ahora, tarea.getFechaEntrega()) - duracionHoras(tiempoMinutos);
			if (margenReal > 48) {
				return 1 - costeTiempo * penalizacionLarga;
			}
		}
		return 1 + tMpt * bonusRapidez - costeTiempo * penalizacionLarga;
	}
	
	private double multiplicadorRevisionGrupo(TareaScoringContext context, double factorFecha, LocalDateTime ahora) {
		if (!context.estaReabierta()) {
			return 1.00;
		}
		Tarea tarea = context.getTarea();
		if (tarea.getFechaEntrega() == null) {
			return 1.00;
		}
		double horasHastaEntrega = horasEntre(ahora, tarea.getFechaEntrega());
		boolean ventanaProxima = (horasHastaEntrega >= 0 && horasHastaEntrega <= 48)
				|| (horasHastaEntrega < 0 && Math.abs(horasHastaEntrega) <= 24);
		if (!ventanaProxima || factorFecha < 0) {
			return 1.00;
		}
		return 1 + Math.max(factorFecha, 0) * 0.10;
	}
	
	private double bonusVentanaCritica(Tarea tarea, int tiempoMinutos, LocalDateTime ahora) {
		if (tarea.getFechaEntrega() == null) {
			return 0.0;
		}
		double horasHastaEntrega = horasEntre(ahora, tarea.getFechaEntrega());
		if (horasHastaEntrega >= 0) {
			double margenReal = horasHastaEntrega - duracionHoras(tiempoMinutos);
			return Math.min(30, bonusVentanaFecha(horasHastaEntrega) + bonusMargenReal(margenReal));
		}
		double horasVencida = Math.abs(horasHastaEntrega);
		double bonusVencimiento = bonusVencimientoReciente(horasVencida);
		if (horasVencida <= 24) {
			return Math.min(24, bonusVencimiento + bonusRescateRapido(tiempoMinutos));
		}
		return bonusVencimiento;
	}
	
	private double bonusSinFecha(Tarea tarea, Prioridad prioridad, int tiempoMinutos) {
		if (tarea.getFechaEntrega() != null) {
			return 0.0;
		}
		double base = switch (prioridad) {
		case IMPRESCINDIBLE -> 12;
		case ALTA -> 6;
		case MEDIA -> 2;
		case BAJA -> 0;
		};
		return base * factorTiempoSinFecha(tiempoMinutos);
	}
	
	private double bonusRapidez(Prioridad prioridad) {
		return switch (prioridad) {
		case BAJA -> 0.10;
		case MEDIA -> 0.08;
		case ALTA -> 0.04;
		case IMPRESCINDIBLE -> 0.05;
		};
	}
	
	private double penalizacionLarga(Prioridad prioridad) {
		return switch (prioridad) {
		case BAJA -> 0.20;
		case MEDIA -> 0.12;
		case ALTA -> 0.05;
		case IMPRESCINDIBLE -> 0.00;
		};
	}
	
	private double bonusVentanaFecha(double horas) {
		if (horas <= 1) return 12;
		if (horas <= 3) return 11;
		if (horas <= 6) return 10;
		if (horas <= 12) return 9;
		if (horas <= 18) return 6;
		if (horas <= 24) return 4;
		if (horas <= 30) return 2;
		if (horas <= 36) return 1;
		return 0;
	}
	
	private double bonusMargenReal(double margenReal) {
		if (margenReal <= 0) return 20;
		if (margenReal <= 1) return 18;
		if (margenReal <= 3) return 16;
		if (margenReal <= 6) return 10;
		if (margenReal <= 12) return 6;
		if (margenReal <= 24) return 3;
		return 0;
	}
	
	private double bonusVencimientoReciente(double horasVencida) {
		if (horasVencida <= 3) return 14;
		if (horasVencida <= 6) return 12;
		if (horasVencida <= 12) return 9;
		if (horasVencida <= 24) return 6;
		if (horasVencida <= 36) return 3;
		if (horasVencida <= 48) return 1;
		return 0;
	}
	
	private double bonusRescateRapido(int tiempoMinutos) {
		if (tiempoMinutos <= 15) return 8;
		if (tiempoMinutos <= 30) return 6;
		if (tiempoMinutos <= 60) return 4;
		if (tiempoMinutos <= 120) return 2;
		return 0;
	}
	
	private double factorTiempoSinFecha(int tiempoMinutos) {
		if (tiempoMinutos <= 15) return 1.00;
		if (tiempoMinutos <= 30) return 0.90;
		if (tiempoMinutos <= 45) return 0.80;
		if (tiempoMinutos <= 60) return 0.70;
		if (tiempoMinutos <= 75) return 0.60;
		if (tiempoMinutos <= 90) return 0.50;
		if (tiempoMinutos <= 105) return 0.42;
		if (tiempoMinutos <= 120) return 0.35;
		if (tiempoMinutos <= 150) return 0.28;
		if (tiempoMinutos <= 180) return 0.22;
		if (tiempoMinutos <= 210) return 0.18;
		if (tiempoMinutos <= 240) return 0.15;
		return 0.10;
	}
	
	private double interpolar(double[][] puntos, double x) {
		if (x <= puntos[0][0]) {
			return puntos[0][1];
		}
		for (int i = 1; i < puntos.length; i++) {
			double x0 = puntos[i - 1][0];
			double y0 = puntos[i - 1][1];
			double x1 = puntos[i][0];
			double y1 = puntos[i][1];
			if (x <= x1) {
				double proporcion = (x - x0) / (x1 - x0);
				return y0 + proporcion * (y1 - y0);
			}
		}
		return puntos[puntos.length - 1][1];
	}
	
	private Prioridad prioridadSegura(Tarea tarea) {
		return tarea.getPrioridad() != null ? tarea.getPrioridad() : Prioridad.BAJA;
	}
	
	private int tiempoMinutosSeguro(Tarea tarea) {
		return tarea.getTiempo() > 0 ? tarea.getTiempo() : 241;
	}
	
	private double duracionHoras(int tiempoMinutos) {
		return tiempoMinutos / 60.0;
	}
	
	private double horasEntre(LocalDateTime inicio, LocalDateTime fin) {
		return Duration.between(inicio, fin).toMillis() / 3_600_000.0;
	}
	
	private String normalizar(String texto) {
		if (texto == null) {
			return "";
		}
		String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		return sinAcentos.trim().toUpperCase(Locale.ROOT);
	}
}
