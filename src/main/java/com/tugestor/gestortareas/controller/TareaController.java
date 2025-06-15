package com.tugestor.gestortareas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.service.TareaService;

@RestController					//Responderá a peticiones HTTP y devovlera JSON
@RequestMapping("/api/tarea")	//Ruta base para los metodos del controlador
public class TareaController {
	
	private final TareaService ts;
	public TareaController(TareaService ts) {
		this.ts = ts;
	}

}
