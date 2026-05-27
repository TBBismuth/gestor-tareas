package com.tugestor.gestortareas.service;

import com.tugestor.gestortareas.dto.RecordatorioInteligenteRequest;
import com.tugestor.gestortareas.dto.RecordatorioTareaResponse;

public interface RecordatorioTareaService {
	RecordatorioTareaResponse configurarRecordatorioInteligente(Long idTarea,
			RecordatorioInteligenteRequest request, String emailUsuario);
	int procesarRecordatoriosInteligentesVencidos();
}
