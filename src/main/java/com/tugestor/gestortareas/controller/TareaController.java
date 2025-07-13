package com.tugestor.gestortareas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
	public List<TareaResponse> listarTareas(Principal principal){	// Principal es una interfaz que representa al usuario autenticado
		List<Tarea> tareas = ts.obtenerTodas(principal.getName());
		return tareas.stream().map(TareaResponse::new)	// Convierte cada Tarea en un TareaResponse
				.toList(); // Recoge el resultado en una nueva lista que es devuelta como respuesta
	}
	
	@Valid
	@PostMapping("/add")
	public TareaResponse aniadirTarea(@RequestBody TareaRequest tareaRequest, Principal principal){
		Tarea tarea = ts.guardarTarea(tareaRequest, principal.getName());
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/{id}")
	public TareaResponse listarTareaId(@PathVariable Long id, Principal principal) {	//Con @PathVariable indico que el {id} de la url es el valor de id
		Tarea tarea = ts.obtenerPorId(id, principal.getName());
		return new TareaResponse(tarea);
	}
	
	@DeleteMapping("/delete/{id}")
	public void eliminarTarea(@PathVariable Long id, Principal principal) {
		ts.eliminarPorId(id, principal.getName());
	}
	
	@Valid
	@PutMapping("/update/{id}")
	public TareaResponse modificarTarea(@PathVariable Long id, @RequestBody TareaRequest tareaRequest, Principal principal) {
		Tarea tarea = ts.actualizarPorId(id, tareaRequest, principal.getName());
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/titulo")
	public List<TareaResponse> listarPorTitulo(Principal principal){
		List<Tarea> tareas = ts.obtenerPorTitulo(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/tiempo")
	public List<TareaResponse> listarPorTiempo(Principal principal){
		List<Tarea> tareas = ts.obtenerPorTiempo(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/prioridad")
	public List<TareaResponse> listarPorPrioridad(Principal principal){
		List<Tarea> tareas = ts.obtenerPorPrioridad(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/fecha")
	public List<TareaResponse> listarPorFechaEntrega(Principal principal){
		List<Tarea> tareas = ts.obtenerPorFechaEntrega(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/{prioridad}")
	public List<TareaResponse> filtrarPorPrioridad(@PathVariable String prioridad, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorPrioridad(prioridad, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/tiempo/{tiempo}")
	public List<TareaResponse> filtrarPorTiempo(@PathVariable int tiempo, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorTiempo(tiempo, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/palabras/{palabrasClave}")
	public List<TareaResponse> filtrarPorPalabrasClave(@PathVariable String palabrasClave, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorPalabrasClave(palabrasClave, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/categoria/{idCategoria}")
	public List<TareaResponse> filtrarPorCategoria(@PathVariable Long idCategoria, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorCategoria(idCategoria, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	/* Metodo deshabilitado hasta tener roles de usuario.
	 * Lo usaré cuando tenga un rol "admin" que pueda ver las tareas de cualquier usuario.
	@GetMapping("/filtrar/usuario/{idUsuario}")
	public List<TareaResponse> filtrarPorUsuario(@PathVariable Long idUsuario) {
		List<Tarea> tareas = ts.filtrarPorUsuario(idUsuario);
		return tareas.stream().map(TareaResponse::new).toList();
	}*/
	
	@GetMapping("/filtrar/estado/{estado}")
	public List<TareaResponse> filtrarPorEstado(@PathVariable Estado estado, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorEstado(estado, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/estado/{id}")
	public Estado obtenerEstadoTarea(@PathVariable Long id, Principal principal) {
		return ts.obtenerEstado(id, principal.getName());
	}
	
	@PatchMapping("/completar/{id}")
	public ResponseEntity<TareaResponse> completarTarea(@PathVariable Long id, Principal principal){
		TareaResponse tareaResponse = ts.marcarTareaCompletada(id, principal.getName());
		return ResponseEntity.ok(tareaResponse);
	}
	
}
