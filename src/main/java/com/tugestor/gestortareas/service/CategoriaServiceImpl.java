package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {
	
	private final CategoriaRepository cr;
	public CategoriaServiceImpl(CategoriaRepository cr) {
		this.cr = cr;
	}
	
	@Override
	public Categoria guardarCategoria(Categoria categoria) {
		return cr.save(categoria);
	}

	@Override
	public List<Categoria> obtenerTodas() {
		return cr.findAll();
	}

	@Override
	public void eliminarPorId(Long id) {
		cr.deleteById(id);
	}

	@Override
	public Categoria actualizarPorId(Long id, Categoria categoriaModificada) {
		Optional<Categoria> categoriaOriginal = cr.findById(id);
		// Optional<Categoria> para evitar errores null. Puede contener una categoria o estar vacía.
		if (categoriaOriginal.isPresent()) {
			Categoria categoria = categoriaOriginal.get();
			categoria.setNombre(categoriaModificada.getNombre());
			categoria.setColor(categoriaModificada.getColor());
			categoria.setIcono(categoriaModificada.getIcono());
			return cr.save(categoria);
		}else {
			throw new RuntimeException("Categoría no encontrada con el id: " + id);
		}
	}

	@Override
	public List<Categoria> obtenerPorNombre(String nombreParcial) {
		return cr.findByNombreIgnoreCaseContaining(nombreParcial);
	}

	@Override
	public Categoria guardarCategoria(CategoriaRequest categoriaRequest) {
		Categoria categoria = new Categoria();
		categoria.setNombre(categoriaRequest.getNombre());
		categoria.setColor(categoriaRequest.getColor());
		categoria.setIcono(categoriaRequest.getIcono());
		return cr.save(categoria);
	}

	@Override
	public Categoria actualizarCategoria(Long id, CategoriaRequest categoriaRequest) {
		Optional<Categoria> categoriaOriginal = cr.findById(id);
		// Optional<Categoria> para evitar errores null. Puede contener una categoria o estar vacía.
		if (categoriaOriginal.isPresent()) {
			Categoria categoria = categoriaOriginal.get();
			categoria.setNombre(categoriaRequest.getNombre());
			categoria.setColor(categoriaRequest.getColor());
			categoria.setIcono(categoriaRequest.getIcono());
			return cr.save(categoria);
		}else {
			throw new RuntimeException("Categoría no encontrada con el id: " + id);
		}
	}


}
