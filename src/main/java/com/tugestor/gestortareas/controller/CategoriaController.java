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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {
	private final CategoriaService cs;
	public CategoriaController(CategoriaService cs) {
		this.cs = cs;
	}
	
	@GetMapping
	public List<CategoriaResponse> listarCategoria(){
		List<Categoria> categorias = cs.obtenerTodas();
		return categorias.stream().map(CategoriaResponse::new).toList();
		// .Stream() convierte la lista en un flujo de datos
		// .map(CategoriaResponse::new) convierte cada Categoria en un CategoriaResponse
		// .toList() recoge el resultado en una nueva lista que es devuelta como respuesta
	}
	
	@Valid
	@PostMapping("/add")
	public CategoriaResponse aniadirCategoria(@RequestBody CategoriaRequest categoriaRequest){
		Categoria categoria = cs.guardarCategoria(categoriaRequest);
		return new CategoriaResponse(categoria);
	}
	
	@DeleteMapping("/delete/{id}")
	public void eliminarCategoria(@PathVariable Long id) {
		cs.eliminarPorId(id);
	}
	
	@Valid
	@PutMapping("/update/{id}")
	public CategoriaResponse modificarCategoria(@PathVariable Long id, @RequestBody CategoriaRequest categoriaRequest) {
		Categoria categoria = cs.actualizarCategoria(id, categoriaRequest);
		return new CategoriaResponse(categoria);
	}
	
	@GetMapping("/nombre/{nombreParcial}")
	public List<CategoriaResponse> listarPorNombre(@PathVariable String nombreParcial) {
		List<Categoria> categorias = cs.obtenerPorNombre(nombreParcial);
		return categorias.stream().map(CategoriaResponse::new).toList();
	}
	
	
}
