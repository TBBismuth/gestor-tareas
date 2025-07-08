package com.tugestor.gestortareas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tugestor.gestortareas.dto.TareaRequest;
import com.tugestor.gestortareas.dto.TareaResponse;
import com.tugestor.gestortareas.model.Estado;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.service.TareaService;

import jakarta.validation.Valid;

@RestController					//Responderá a peticiones HTTP y devovlera JSON
@RequestMapping("/api/tarea")	//Ruta base para los metodos del controlador
public class TareaController {
	
	/* (TareaService ts = new TareaServiceImpl();)
	 * Spring, al detectar que TareaServiceImpl es un @Service(IMPORTANTE) y que el controlador necesita TareaService,
	 * automatiza esta asignacion internamente inyectando la dependencia*/
	private final TareaService ts;
	public TareaController(TareaService ts) {
		this.ts = ts;
	}
	
	@GetMapping("/test")
	public String test() {
		return "endpoint TEST ";
	}
	
	@GetMapping
	public List<TareaResponse> listarTareas(){
		List<Tarea> tareas = ts.obtenerTodas();
		return tareas.stream().map(TareaResponse::new)	// Convierte cada Tarea en un TareaResponse
				.toList(); // Recoge el resultado en una nueva lista que es devuelta como respuesta
	}
	
	@Valid
	@PostMapping("/add")
	public TareaResponse aniadirTarea(@RequestBody TareaRequest tareaRequest){
		Tarea tarea = ts.guardarTarea(tareaRequest);
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/{id}")
	public TareaResponse listarTareaId(@PathVariable Long id) {	//Con @PathVariable indico que el {id} de la url es el valor de id
		Tarea tarea = ts.obtenerPorId(id);
		return new TareaResponse(tarea);
	}
	
	@DeleteMapping("/delete/{id}")
	public void eliminarTarea(@PathVariable Long id) {
		ts.eliminarPorId(id);
	}
	
	@Valid
	@PutMapping("/update/{id}")
	public TareaResponse modificarTarea(@PathVariable Long id, @RequestBody TareaRequest tareaRequest) {
		Tarea tarea = ts.actualizarPorId(id, tareaRequest);
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/titulo")
	public List<TareaResponse> listarPorTitulo(){
		List<Tarea> tareas = ts.obtenerPorTitulo();
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/tiempo")
	public List<TareaResponse> listarPorTiempo(){
		List<Tarea> tareas = ts.obtenerPorTiempo();
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/prioridad")
	public List<TareaResponse> listarPorPrioridad(){
		List<Tarea> tareas = ts.obtenerPorPrioridad();
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/fecha")
	public List<TareaResponse> listarPorFechaEntrega(){
		List<Tarea> tareas = ts.obtenerPorFechaEntrega();
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/{prioridad}")
	public List<TareaResponse> filtrarPorPrioridad(@PathVariable String prioridad) {
		List<Tarea> tareas = ts.filtrarPorPrioridad(prioridad);
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/tiempo/{tiempo}")
	public List<TareaResponse> filtrarPorTiempo(@PathVariable int tiempo) {
		List<Tarea> tareas = ts.filtrarPorTiempo(tiempo);
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/palabras/{palabrasClave}")
	public List<TareaResponse> filtrarPorPalabrasClave(@PathVariable String palabrasClave) {
		List<Tarea> tareas = ts.filtrarPorPalabrasClave(palabrasClave);
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/categoria/{idCategoria}")
	public List<TareaResponse> filtrarPorCategoria(@PathVariable Long idCategoria) {
		List<Tarea> tareas = ts.filtrarPorCategoria(idCategoria);
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/usuario/{idUsuario}")
	public List<TareaResponse> filtrarPorUsuario(@PathVariable Long idUsuario) {
		List<Tarea> tareas = ts.filtrarPorUsuario(idUsuario);
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/estado/{id}")
	public Estado obtenerEstadoTarea(@PathVariable Long id) {
		return ts.obtenerEstado(id);
	}
}
