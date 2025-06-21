package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.model.Categoria;

public interface CategoriaService {
	Categoria guardarCategoria(Categoria categoria);
	List<Categoria> obtenerTodas();
	void eliminarPorId(Long id);
	Categoria actualizarPorId(Long id, Categoria categoriaModificada);
	List<Categoria> obtenerPorNombre(String nombre);

}
