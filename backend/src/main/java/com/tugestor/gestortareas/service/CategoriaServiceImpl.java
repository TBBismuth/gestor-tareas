package com.tugestor.gestortareas.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tugestor.gestortareas.dto.CategoriaRequest;
import com.tugestor.gestortareas.exception.CategoriaProtegidaException;
import com.tugestor.gestortareas.model.Categoria;
import com.tugestor.gestortareas.model.Tarea;
import com.tugestor.gestortareas.model.Usuario;
import com.tugestor.gestortareas.repository.CategoriaRepository;
import com.tugestor.gestortareas.repository.TareaRepository;
import com.tugestor.gestortareas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaServiceImpl implements CategoriaService {
	
	private final CategoriaRepository cr;
	private final TareaRepository tr;
	private final UsuarioRepository ur;
	public CategoriaServiceImpl(CategoriaRepository cr, TareaRepository tr, UsuarioRepository ur) {
		this.cr = cr;
		this.tr = tr;
		this.ur = ur;
	}

	private Usuario getUsuarioActual() {	//Metodo helper para el resto
		//Obtengo el email del usuario autenticado desde Spring Security (SecurityContext > Authentication > username)
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ur.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado: " + email));
	}
	
	@Override
	public Categoria guardarCategoria(Categoria categoria) {
		return cr.save(categoria);
	}

	@Override
	public List<Categoria> obtenerTodas() {
		Usuario actual = getUsuarioActual();
		return cr.findAllByUsuario(actual);
	}

	@Transactional //Para que no pete a mitad de un desvinculado-eliminacion y se quede a medias
	@Override
	public void eliminarPorId(Long id) {
		Categoria categoria = cr.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));
		//Comprobar que la categoria pertenece al usuario
		Usuario actual = getUsuarioActual();
		if (categoria.getUsuario() == null || !categoria.getUsuario().getIdUsuario().equals(actual.getIdUsuario())) {
			throw new EntityNotFoundException("Categoría no encontrada para este usuario: " + id);
		}
		//Comprobar que no sea una categoria protegida
		if (categoria.isProtegida()) {
			throw new CategoriaProtegidaException("No se puede eliminar una categoría base.");
		}
		// Desvincular tareas asociadas
		List<Tarea> tareasAsociadas = tr.findByCategoria_IdCategoria(id);
		for (Tarea tarea : tareasAsociadas) {
			tarea.setCategoria(null);
			tr.save(tarea);
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
		Usuario actual = getUsuarioActual();
		return cr.findByUsuarioAndNombreIgnoreCaseContaining(actual, nombreParcial);
	}
	
	@Override
	public Categoria guardarCategoria(CategoriaRequest categoriaRequest) {
		//Comprobar que la categoria pertenece al usuario
		Usuario usuario = getUsuarioActual();
		if (cr.existsByUsuarioAndNombreIgnoreCase(usuario, categoriaRequest.getNombre())) {
			throw new jakarta.persistence.EntityExistsException(
					"Ya existe una categoría con ese nombre para este usuario"
					);
		}
		
		Categoria categoria = new Categoria();
		categoria.setNombre(categoriaRequest.getNombre());
		categoria.setColor(categoriaRequest.getColor());
		categoria.setIcono(categoriaRequest.getIcono());
		categoria.setUsuario(usuario);
		return cr.save(categoria);
	}
	
	@Override
	public Categoria actualizarCategoria(Long id, CategoriaRequest categoriaRequest) {
		Optional<Categoria> categoriaOriginal = cr.findById(id);
		// Optional<Categoria> para evitar errores null. Puede contener una categoria o estar vacía.
		if (categoriaOriginal.isPresent()) {
			Categoria categoria = categoriaOriginal.get();

			//Verificar que la categoría es del usuario autenticado
			Usuario usuario = getUsuarioActual();
			if (categoria.getUsuario() == null || !Objects.equals(categoria.getUsuario().getIdUsuario(), usuario.getIdUsuario())) {
				throw new EntityNotFoundException("Categoría no encontrada para este usuario: " + id);
			}
			//Verificar que la categoria no está protegida
			if (categoria.isProtegida()) {
				throw new CategoriaProtegidaException(
						"No se puede editar una categoría base (" + categoria.getNombre() + ")."
						);
			}
			//Evitar duplicado de nombre dentro del mismo usuario
			String nuevoNombre = categoriaRequest.getNombre();
			if (nuevoNombre != null && !nuevoNombre.equalsIgnoreCase(categoria.getNombre()) && cr.existsByUsuarioAndNombreIgnoreCase(usuario, nuevoNombre)) {
				throw new jakarta.persistence.EntityExistsException(
						"Ya existe una categoría con ese nombre para este usuario"
						);
			}

			categoria.setNombre(categoriaRequest.getNombre());
			categoria.setColor(categoriaRequest.getColor());
			categoria.setIcono(categoriaRequest.getIcono());
			return cr.save(categoria);
		} else {
			throw new EntityNotFoundException("Categoría no encontrada con el id: " + id);
		}
	}



}
