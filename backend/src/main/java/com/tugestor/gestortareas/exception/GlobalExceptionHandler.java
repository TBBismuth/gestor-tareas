package com.tugestor.gestortareas.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)	//Captura los RuntimeException lanzados desde cualquier controlador
	public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
		//Devuelvo una respuesta HTTP 400 (badRequest) con el mensaje como cuerpo
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Solicitud inválida."));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)	//Captura errores de validación disparados por @Valid en los controladores
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		//Mapa para almacenar los errores de validación campo por campo
		Map<String, String> errors = new HashMap<>();
		//Recorremos todos los errores de validación individuales
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String nombreCampo = error.getField(); //Nombre del campo que ha fallado
			String mensajeError = error.getDefaultMessage(); //Mensaje personalizado definido en la anotación con message = "..."
			errors.put(nombreCampo, mensajeError); //Asociamos campo al mensaje
		});
		// Devolvemos todos los errores agrupados en una respuesta HTTP 400
		return ResponseEntity.badRequest()
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Datos de entrada no válidos.", errors));
	}
	
	@ExceptionHandler(ConstraintViolationException.class) // Captura excepciones de validación que ocurren al guardar entidades con JPA
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
		//Mapa para almacenar los errores campo por campo
		Map<String, String> errors = new HashMap<>();
		//Recorremos todas las violaciones de restricciones
		ex.getConstraintViolations().forEach(violation -> {
			String nombreCampo = violation.getPropertyPath().toString(); //Nombre del campo que ha fallado
			String mensajeError = violation.getMessage(); //Mensaje de error asociado (desde la anotación)
			errors.put(nombreCampo, mensajeError); //Añadimos el campo y su mensaje al mapa de errores
		});
		// Devolvemos todos los errores con un código HTTP 400
		return ResponseEntity.badRequest()
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Datos de entrada no válidos.", errors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido o está mal formado.", null));
	}
	
	@ExceptionHandler(EntityNotFoundException.class)// Captura excepciones de entidad no encontrada
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
		// Mapa para representar la respuesta de error
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(error(HttpStatus.NOT_FOUND, ex.getMessage(), "Recurso no encontrado."));
	}

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<ErrorResponse> handleEntityExistsException(EntityExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error(HttpStatus.CONFLICT, ex.getMessage(), "El recurso ya existe."));
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Solicitud inválida."));
	}

	@ExceptionHandler(AccessDeniedException.class)	// Captura excepciones de acceso denegado
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(error(HttpStatus.FORBIDDEN, ex.getMessage(), "No tienes permiso para realizar esta acción."));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)// Captura errores de tipo de argumento en las peticiones ej: enum incorrecto
	public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		String mensaje = "El valor indicado no es válido para el parámetro '" + ex.getName() + "'.";
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, mensaje, null));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, ex.getMessage(), "El valor indicado no es válido."));
	}

	@ExceptionHandler(UsernameNotFoundException.class)	// Captura excepciones de usuario no encontrado, generalmente en login
	public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas.", null));	// Mensaje generico para no revelar si es usuario o contraseña incorrectos
	}
	
	@ExceptionHandler(EmailDuplicadoException.class)
	public ResponseEntity<ErrorResponse> handleEmailDuplicadoException(EmailDuplicadoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error(HttpStatus.CONFLICT, ex.getMessage(), "Ya existe un usuario con ese email."));
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas.", null));
	}
	
	@ExceptionHandler(CategoriaProtegidaException.class)
	public ResponseEntity<ErrorResponse> handleCategoriaProtegida(CategoriaProtegidaException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error(HttpStatus.CONFLICT, ex.getMessage(), "La categoría está protegida."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", null));
	}

	private ErrorResponse error(HttpStatus status, String mensaje, String mensajePorDefecto) {
		String mensajeSeguro = (mensaje == null || mensaje.isBlank()) ? mensajePorDefecto : mensaje;
		return new ErrorResponse(status.value(), mensajeSeguro);
	}
	
}
