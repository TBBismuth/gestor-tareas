package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaServiceImpl implements CategoriaService {
	
	private final CategoriaRepository cr;
	private final TareaRepository tareaRepository;
	public CategoriaServiceImpl(CategoriaRepository cr, TareaRepository tareaRepository) {
		this.cr = cr;
		this.tareaRepository = tareaRepository;
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
		Categoria categoria = cr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));
		if (categoria.isProtegida()) {
			throw new AccessDeniedException("No se puede eliminar una categoría base.");
		}
		// Desvincular tareas asociadas
		List<Tarea> tareasAsociadas = tareaRepository.findByCategoria_IdCategoria(id);

		for (Tarea tarea : tareasAsociadas) {
		    tarea.setCategoria(null);
		    tareaRepository.save(tarea);
		}

		cr.delete(categoria);
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
			throw new EntityNotFoundException("Categoría no encontrada con el id: " + id);
		}
	}


}
