package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.model.Categoria;

public interface CategoriaService {
	Categoria guardarCategoria(Categoria categoria);
	Categoria guardarCategoria(CategoriaRequest categoria);
	List<Categoria> obtenerTodas();
	void eliminarPorId(Long id);
	Categoria actualizarPorId(Long id, Categoria categoriaModificada);
	Categoria actualizarCategoria(Long id, CategoriaRequest categoriaRequest);
	List<Categoria> obtenerPorNombre(String nombre);

}
