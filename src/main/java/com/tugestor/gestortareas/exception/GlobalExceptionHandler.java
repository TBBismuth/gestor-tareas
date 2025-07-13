package com.tugestor.gestortareas.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)	//Captura los RuntimeException lanzados desde cualquier controlador
	public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
		//Mapa para representar la respuesta en formato JSON
		Map<String, String> error = new HashMap<>();
		//Inserto en el JSON el mensaje de error que contiene la excepcion
		error.put("error", ex.getMessage());
		//Devuelvo una respuesta HTTP 400 (badRequest) con el mensaje como cuerpo
		return ResponseEntity.badRequest().body(error);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)	//Captura errores de validación disparados por @Valid en los controladores
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		//Mapa para almacenar los errores de validación campo por campo
		Map<String, String> errors = new HashMap<>();
		//Recorremos todos los errores de validación individuales
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String nombreCampo = error.getField(); //Nombre del campo que ha fallado
			String mensajeError = error.getDefaultMessage(); //Mensaje personalizado definido en la anotación con message = "..."
			errors.put(nombreCampo, mensajeError); //Asociamos campo al mensaje
		});
		// Devolvemos todos los errores agrupados en una respuesta HTTP 400
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler(ConstraintViolationException.class) // Captura excepciones de validación que ocurren al guardar entidades con JPA
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
		//Mapa para almacenar los errores campo por campo
		Map<String, String> errors = new HashMap<>();
		//Recorremos todas las violaciones de restricciones
		ex.getConstraintViolations().forEach(violation -> {
			String nombreCampo = violation.getPropertyPath().toString(); //Nombre del campo que ha fallado
			String mensajeError = violation.getMessage(); //Mensaje de error asociado (desde la anotación)
			errors.put(nombreCampo, mensajeError); //Añadimos el campo y su mensaje al mapa de errores
		});
		// Devolvemos todos los errores con un código HTTP 400
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)// Captura excepciones de entidad no encontrada
	public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
		// Mapa para representar la respuesta de error
		Map<String, String> error = new HashMap<>();
		error.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)	// Captura excepciones de acceso denegado
	public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)// Captura errores de tipo de argumento en las peticiones ej: enum incorrecto
	public ResponseEntity<Map<String, String>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "El valor '" + ex.getValue() + "' no es válido para el parámetro '" + ex.getName() + "'.");
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(UsernameNotFoundException.class)	// Captura excepciones de usuario no encontrado, generalmente en login
	public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Credenciales inválidas.");	// Mensaje generico para no revelar si es usuario o contraseña incorrectos
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}


}
