package com.tugestor.gestortareas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.service.TareaService;

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
	public List<Tarea> listarTareas(){
		return ts.obtenerTodas();
	}
	
	@PostMapping("/add")
	public Tarea aniadirTarea(@RequestBody Tarea tarea){
		return ts.guardarTarea(tarea);
	}
	
	@GetMapping("/{id}")
	public Tarea listarTareaId(@PathVariable Long id) {	//Con @PathVariable indico que el {id} de la url es el valor de id
		return ts.obtenerPorId(id);
	}
	
	@DeleteMapping("/delete/{id}")
	public void eliminarTarea(@PathVariable Long id) {
		ts.eliminarPorId(id);
	}
	
	@PutMapping("/update/{id}")
	public Tarea modificarTarea(@PathVariable Long id, @RequestBody Tarea tareaModificada) {
		return ts.actualizarPorId(id, tareaModificada);
		
	}
	
	@GetMapping("/titulo")
	public List<Tarea> listarPorTitulo(){
		return ts.obtenerPorTitulo();
	}
	
	@GetMapping("/tiempo")
	public List<Tarea> listarPorTiempo(){
		return ts.obtenerPorTiempo();
	}
	
	@GetMapping("/prioridad")
	public List<Tarea> listarPorPrioridad(){
		return ts.obtenerPorPrioridad();
	}
	
	@GetMapping("/fecha")
	public List<Tarea> listarPorFechaEntrega(){
		return ts.obtenerPorFechaEntrega();
	}
	
	@GetMapping("/filtrar/{prioridad}")
	public List<Tarea> filtrarPorPrioridad(@PathVariable String prioridad) {
		return ts.filtrarPorPrioridad(prioridad);
	}
	
	@GetMapping("/filtrar/tiempo/{tiempo}")
	public List<Tarea> filtrarPorTiempo(@PathVariable int tiempo) {
		return ts.filtrarPorTiempo(tiempo);
	}
	
	@GetMapping("/filtrar/palabras/{palabrasClave}")
	public List<Tarea> filtrarPorPalabrasClave(@PathVariable String palabrasClave) {
		return ts.filtrarPorPalabrasClave(palabrasClave);
	}
	
	@GetMapping("/filtrar/categoria/{idCategoria}")
	public List<Tarea> filtrarPorCategoria(@PathVariable Long idCategoria) {
		return ts.filtrarPorCategoria(idCategoria);
	}
}
