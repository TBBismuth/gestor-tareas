package com.tugestor.gestortareas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tugestor.gestortareas.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

}