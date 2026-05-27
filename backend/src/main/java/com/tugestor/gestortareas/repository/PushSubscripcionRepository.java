package com.tugestor.gestortareas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tugestor.gestortareas.model.PushSubscripcion;

public interface PushSubscripcionRepository extends JpaRepository<PushSubscripcion, Long> {
	Optional<PushSubscripcion> findByEndpoint(String endpoint);
}
