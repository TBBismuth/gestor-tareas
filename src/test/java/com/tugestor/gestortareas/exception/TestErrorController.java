package com.tugestor.gestortareas.exception;

import java.util.Set;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tugestor.gestortareas.controller.TestValidationController.DummyRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jakarta.validation.metadata.ConstraintDescriptor;

@RestController
@RequestMapping("/error")
public class TestErrorController {

	@GetMapping("/runtime")
	public void runtime() {
		throw new RuntimeException("Error simulado");
	}

	@PostMapping("/validation")
	public void validation(@Valid @RequestBody DummyRequest request) {
		// No hace falta nada: se dispara MethodArgumentNotValidException si el body es inválido
	}

	@GetMapping("/constraint")
	public void constraint() {
		throw new ConstraintViolationException(
				Set.of(
						new ConstraintViolation<Object>() {
							@Override
							public String getMessage() { return "Error de restricción simulado"; }
							@Override
							public String getMessageTemplate() { return null; }
							@Override
							public Object getRootBean() { return null; }
							@Override
							public Class<Object> getRootBeanClass() { return null; }
							@Override
							public Object getLeafBean() { return null; }
							@Override
							public Object[] getExecutableParameters() { return new Object[0]; }
							@Override
							public Object getExecutableReturnValue() { return null; }
							@Override
							public Path getPropertyPath() { return PathImpl.createPathFromString("campo"); }
							@Override
							public Object getInvalidValue() { return null; }
							@Override
							public ConstraintDescriptor<?> getConstraintDescriptor() { return null; }
							@Override
							public <U> U unwrap(Class<U> type) { return null; }
						}
						)
				);
	}
}

