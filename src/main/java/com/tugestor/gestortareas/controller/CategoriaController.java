package com.tugestor.gestortareas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.dto.CategoriaResponse;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {
	private final CategoriaService cs;
	public CategoriaController(CategoriaService cs) {
		this.cs = cs;
	}
	
	@GetMapping
	@Operation(
			summary = "Listar todas las categorías",
			description = "Devuelve un listado de todas las categorías existentes."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de categorías obtenido correctamente")
	})
	public List<CategoriaResponse> listarCategoria(){
		List<Categoria> categorias = cs.obtenerTodas();
		return categorias.stream().map(CategoriaResponse::new).toList();
		// .Stream() convierte la lista en un flujo de datos
		// .map(CategoriaResponse::new) convierte cada Categoria en un CategoriaResponse
		// .toList() recoge el resultado en una nueva lista que es devuelta como respuesta
	}

	@Valid
	@PostMapping("/add")
	@Operation(
			summary = "Añadir una nueva categoría",
			description = "Crea una nueva categoría con los datos proporcionados."
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Categoría creada correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos de la categoría inválidos")
	})
	public CategoriaResponse aniadirCategoria(@RequestBody CategoriaRequest categoriaRequest){
		Categoria categoria = cs.guardarCategoria(categoriaRequest);
		return new CategoriaResponse(categoria);
	}

	@DeleteMapping("/delete/{id}")
	@Operation(
			summary = "Eliminar una categoría",
			description = "Elimina la categoría indicada por su ID."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la categoría a eliminar", example = "5")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
		@ApiResponse(responseCode = "404", description = "Categoría no encontrada")
	})
	public void eliminarCategoria(@PathVariable Long id) {
		cs.eliminarPorId(id);
	}
	
	@Valid
	@PutMapping("/update/{id}")
	@Operation(
			summary = "Modificar una categoría",
			description = "Actualiza los datos de una categoría existente."
			)
	@Parameters({
		@Parameter(name = "id", description = "ID de la categoría a modificar", example = "3")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
		@ApiResponse(responseCode = "400", description = "Datos de la categoría inválidos"),
		@ApiResponse(responseCode = "404", description = "Categoría no encontrada")
	})
	public CategoriaResponse modificarCategoria(@PathVariable Long id, @RequestBody CategoriaRequest categoriaRequest) {
		Categoria categoria = cs.actualizarCategoria(id, categoriaRequest);
		return new CategoriaResponse(categoria);
	}

	@GetMapping("/nombre/{nombreParcial}")
	@Operation(
			summary = "Buscar categorías por nombre parcial",
			description = "Devuelve las categorías cuyo nombre contenga la cadena indicada."
			)
	@Parameters({
		@Parameter(name = "nombreParcial", description = "Texto a buscar en el nombre de la categoría", example = "Trabajo")
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Listado de categorías obtenido correctamente")
	})
	public List<CategoriaResponse> listarPorNombre(@PathVariable String nombreParcial) {
		List<Categoria> categorias = cs.obtenerPorNombre(nombreParcial);
		return categorias.stream().map(CategoriaResponse::new).toList();
	}
	
	
}
