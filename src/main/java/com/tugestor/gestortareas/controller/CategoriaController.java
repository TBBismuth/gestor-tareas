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

import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.service.CategoriaService;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {
	private final CategoriaService cs;
	public CategoriaController(CategoriaService cs) {
		this.cs = cs;
	}
	
	@GetMapping
	public List<Categoria> listarCategoria(){
		return cs.obtenerTodas();
	}
	
	@PostMapping("/add")
	public Categoria aniadirCategoria(@RequestBody Categoria categoria){
		return cs.guardarCategoria(categoria);
	}
	
	@DeleteMapping("/delete/{id}")
	public void eliminarCategoria(@PathVariable Long id) {
		cs.eliminarPorId(id);
	}
	
	@PutMapping("/update/{id}")
	public Categoria modificarCategoria(@PathVariable Long id, @RequestBody Categoria categoriaModificada) {
		return cs.actualizarPorId(id, categoriaModificada);
	}
	
	@GetMapping("/nombre/{nombreParcial}")
	public List<Categoria> listarPorNombre(@PathVariable String nombreParcial) {
		return cs.obtenerPorNombre(nombreParcial);
	}
	
	
}
