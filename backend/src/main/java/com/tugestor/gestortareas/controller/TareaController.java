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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@Operation(
			summary = "Listar todas las tareas del usuario autenticado",
			description = "Devuelve una lista con todas las tareas asociadas al usuario actualmente autenticado. Requiere un token JWT válido en la cabecera Authorization."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarTareas(Principal principal){	// Principal es una interfaz que representa al usuario autenticado
		List<Tarea> tareas = ts.obtenerTodas(principal.getName());
		return tareas.stream().map(TareaResponse::new)	// Convierte cada Tarea en un TareaResponse
				.toList(); // Recoge el resultado en una nueva lista que es devuelta como respuesta
	}

	@PostMapping("/add")
	@Operation(
			summary = "Añadir una nueva tarea",
			description = "Crea una nueva tarea asociada al usuario autenticado. Requiere un token JWT válido."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tarea creada correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos de la tarea inválidos"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public TareaResponse aniadirTarea(@Valid @RequestBody TareaRequest tareaRequest, Principal principal){
		Tarea tarea = ts.guardarTarea(tareaRequest, principal.getName());
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/{id}")
	@Operation(
			summary = "Obtener una tarea por ID",
			description = "Devuelve los detalles de una tarea específica si pertenece al usuario autenticado."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la tarea", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tarea encontrada correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
		@ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
	})
	public TareaResponse listarTareaId(@PathVariable Long id, Principal principal) {	//Con @PathVariable indico que el {id} de la url es el valor de id
		Tarea tarea = ts.obtenerPorId(id, principal.getName());
		return new TareaResponse(tarea);
	}

	@DeleteMapping("/delete/{id}")
	@Operation(
			summary = "Eliminar una tarea",
			description = "Elimina una tarea existente si pertenece al usuario autenticado."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la tarea a eliminar", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tarea eliminada correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
		@ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
	})
	public void eliminarTarea(@PathVariable Long id, Principal principal) {
		ts.eliminarPorId(id, principal.getName());
	}

	@PutMapping("/update/{id}")
	@Operation(
			summary = "Modificar una tarea existente",
			description = "Actualiza los datos de una tarea si pertenece al usuario autenticado. Requiere un token JWT válido."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la tarea a modificar", example = "1")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tarea actualizada correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos de la tarea inválidos o no existe"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
		@ApiResponse(responseCode = "403", description = "Tarea no pertenece al usuario")
	})
	public TareaResponse modificarTarea(@PathVariable Long id,@Valid @RequestBody TareaRequest tareaRequest, Principal principal) {
		Tarea tarea = ts.actualizarPorId(id, tareaRequest, principal.getName());
		return new TareaResponse(tarea);
	}
	
	@GetMapping("/titulo")
	@Operation(
			summary = "Listar tareas ordenadas por título",
			description = "Devuelve las tareas del usuario autenticado ordenadas alfabéticamente por título."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarPorTitulo(Principal principal){
		List<Tarea> tareas = ts.obtenerPorTitulo(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}

	@GetMapping("/tiempo")
	@Operation(
			summary = "Listar tareas ordenadas por tiempo estimado",
			description = "Devuelve las tareas del usuario autenticado ordenadas según su tiempo estimado de realización."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarPorTiempo(Principal principal){
		List<Tarea> tareas = ts.obtenerPorTiempo(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/prioridad")
	@Operation(
			summary = "Listar tareas ordenadas por prioridad",
			description = "Devuelve las tareas del usuario autenticado ordenadas por prioridad (BAJA, MEDIA, ALTA, IMPRESCINDIBLE)."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarPorPrioridad(Principal principal){
		List<Tarea> tareas = ts.obtenerPorPrioridad(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}

	@GetMapping("/fecha")
	@Operation(
			summary = "Listar tareas ordenadas por fecha de entrega",
			description = "Devuelve las tareas del usuario autenticado ordenadas cronológicamente por fecha de entrega."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarPorFechaEntrega(Principal principal){
		List<Tarea> tareas = ts.obtenerPorFechaEntrega(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}

	@GetMapping("/filtrar/{prioridad}")
	@Operation(
			summary = "Filtrar tareas por prioridad",
			description = "Devuelve las tareas del usuario autenticado que tienen la prioridad especificada."
			)
	@Parameters({
		@Parameter(name = "prioridad", description = "Prioridad de la tarea (BAJA, MEDIA, ALTA, IMPRESINDIBLE)", example = "ALTA")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas filtradas correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> filtrarPorPrioridad(@PathVariable String prioridad, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorPrioridad(prioridad, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/tiempo/{tiempo}")
	@Operation(
			summary = "Filtrar tareas por tiempo estimado",
			description = "Devuelve las tareas del usuario autenticado cuyo tiempo estimado coincide con el valor indicado."
			)
	@Parameters({
		@Parameter(name = "tiempo", description = "Tiempo estimado en minutos", example = "60")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas filtradas correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> filtrarPorTiempo(@PathVariable int tiempo, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorTiempo(tiempo, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}

	@GetMapping("/filtrar/palabras/{palabrasClave}")
	@Operation(
			summary = "Filtrar tareas por palabras clave en el título o descripción",
			description = "Devuelve las tareas del usuario autenticado que contienen las palabras clave indicadas tanto en el titulo como en la descripción."
			)
	@Parameters({
		@Parameter(name = "palabrasClave", description = "Palabras clave a buscar separadas por espacio", example = "mi informe")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas filtradas correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> filtrarPorPalabrasClave(@PathVariable String palabrasClave, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorPalabrasClave(palabrasClave, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
	@GetMapping("/filtrar/categoria/{idCategoria}")
	@Operation(
			summary = "Filtrar tareas por categoría",
			description = "Devuelve las tareas del usuario autenticado que pertenecen a la categoría especificada."
			)
	@Parameters({
		@Parameter(name = "categoria", description = "Nombre de la categoría", example = "Trabajo/Estudios")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas filtradas correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
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
	@Operation(
			summary = "Filtrar tareas por estado",
			description = "Devuelve las tareas del usuario autenticado que tienen el estado especificado (COMPLETADA, EN_CURSO, VENCIDA, COMPLETADA_CON_RETRASO, SIN_FECHA)."
			)
	@Parameters({
		@Parameter(name = "estado", description = "Estado de la tarea", example = "EN_CURSO")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas filtradas correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> filtrarPorEstado(@PathVariable Estado estado, Principal principal) {
		List<Tarea> tareas = ts.filtrarPorEstado(estado, principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}

	@GetMapping("/estado/{id}")
	@Operation(
			summary = "Obtener estado de una tarea por ID",
			description = "Devuelve el estado actual de la tarea especificada si pertenece al usuario autenticado."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la tarea", example = "5")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Estado de la tarea obtenido correctamente"),
		@ApiResponse(responseCode = "400", description = "Tarea no encontrada"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public Estado obtenerEstadoTarea(@PathVariable Long id, Principal principal) {
		return ts.obtenerEstado(id, principal.getName());
	}

	@PatchMapping("/completar/{id}")
	@Operation(
			summary = "Marcar una tarea como completada",
			description = "Marca la tarea como completada si pertenece al usuario autenticado. Requiere un token JWT válido."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la tarea a marcar como completada", example = "7")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Tarea marcada como completada correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido"),
		@ApiResponse(responseCode = "403", description = "La tarea no pertenece al usuario autenticado"),
		@ApiResponse(responseCode = "404", description = "Tarea no encontrada")
	})
	public ResponseEntity<TareaResponse> completarTarea(@PathVariable Long id, Principal principal){
		TareaResponse tareaResponse = ts.marcarTareaCompletada(id, principal.getName());
		return ResponseEntity.ok(tareaResponse);
	}

	@GetMapping("/hoy")
	@Operation(
			summary = "Obtener tareas programadas para hoy",
			description = "Devuelve todas las tareas del usuario autenticado cuya fecha de entrega sea en el dia de hoy."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de tareas de hoy obtenido correctamente"),
		@ApiResponse(responseCode = "401", description = "No autenticado o token inválido")
	})
	public List<TareaResponse> listarTareasHoy(Principal principal) {
		List<Tarea> tareas = ts.obtenerTareasHoy(principal.getName());
		return tareas.stream().map(TareaResponse::new).toList();
	}
	
}
