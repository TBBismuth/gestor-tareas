package com.tugestor.gestortareas.service;

import java.util.List;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.model.Categoria;

public interface CategoriaService {
	/**
	 * @deprecated Use {@link #guardarCategoria(CategoriaRequest)} instead
	 */
	@Deprecated(since="2.0", forRemoval=false)
	Categoria guardarCategoria(Categoria categoria);
	Categoria guardarCategoria(CategoriaRequest categoriaRequest);
	List<Categoria> obtenerTodas();
	void eliminarPorId(Long id);
	/**
	 * @deprecated Use {@link #actualizarCategoria(Long, CategoriaRequest)} instead
	 */
	@Deprecated(since="2.0", forRemoval=false)
	Categoria actualizarPorId(Long id, Categoria categoriaModificada);
	Categoria actualizarCategoria(Long id, CategoriaRequest categoriaRequest);
	List<Categoria> obtenerPorNombre(String nombre);

}
