export function mapTaskResponseToCardTask(task, categoryById = new Map()) {
  const category = task.idCategoria ? categoryById.get(Number(task.idCategoria)) : null;
  const categoryName = category?.nombre || task.categoriaNombre;

  return {
    idTarea: task.idTarea,
    titulo: task.titulo,
    descripcion: task.descripcion,
    tiempo: task.tiempo,
    prioridad: task.prioridad,
    fechaEntrega: task.fechaEntrega,
    fechaAgregado: task.fechaAgregado,
    completada: task.completada,
    fechaCompletada: task.fechaCompletada,
    estado: task.estado,
    idCategoria: task.idCategoria,
    categoriaNombre: categoryName,
    nombreCategoria: categoryName,
    colorCategoria: category?.color || null,
    iconoCategoria: category?.icono || null,
    idUsuario: task.idUsuario,
    emailUsuarioQueCompleta: task.emailUsuarioQueCompleta,
    recordatorioInteligenteActivo:
      task.recordatorioInteligenteActivo === true || task.recordatorioActivo === true,
    origenTarea: "PERSONAL",
    idGrupoOrigen: null,
    nombreGrupoOrigen: null,
    estadoRevisionAsignacion: null,
    comentarioRevision: null,
    tipoAsignacion: null,
  };
}

export function mapAssignedGroupTaskResponseToCardTask(task, categoryById = new Map()) {
  const category = task.idCategoria ? categoryById.get(Number(task.idCategoria)) : null;
  const categoryName = task.nombreCategoria || task.categoriaNombre || category?.nombre;

  return {
    idTarea: task.idTarea,
    titulo: task.titulo,
    descripcion: task.descripcion,
    tiempo: task.tiempo,
    prioridad: task.prioridad,
    fechaEntrega: task.fechaEntrega,
    fechaAgregado: task.fechaAgregado,
    completada: task.completada,
    fechaCompletada: task.fechaCompletada,
    estado: task.estado,
    idCategoria: task.idCategoria || null,
    categoriaNombre: categoryName,
    nombreCategoria: categoryName,
    colorCategoria: task.colorCategoria || task.categoriaColor || category?.color || null,
    iconoCategoria: task.iconoCategoria || task.categoriaIcono || category?.icono || null,
    idUsuario: task.idUsuario,
    emailUsuarioQueCompleta: task.emailUsuarioQueCompleta,
    recordatorioInteligenteActivo:
      task.recordatorioInteligenteActivo === true || task.recordatorioActivo === true,
    origenTarea: "GRUPO",
    idGrupoOrigen: task.idGrupoOrigen || null,
    nombreGrupoOrigen: task.nombreGrupoOrigen || null,
    idAsignacionGrupo: task.idAsignacionGrupo || null,
    idAsignacionGrupoMiembro: task.idAsignacionGrupoMiembro || null,
    estadoRevisionAsignacion: task.estadoRevisionAsignacion || null,
    comentarioRevision: task.comentarioRevision || null,
    tipoAsignacion: task.tipoAsignacion || null,
    fechaAsignacion: task.fechaAsignacion || null,
    fechaEntregaInicial: task.fechaEntregaInicial || null,
    fechaEntregaActual: task.fechaEntregaActual || null,
    fechaRevision: task.fechaRevision || null,
  };
}

export function mapTaskResponsesToCardTasks(tasks, categories = [], assignedGroupTasks = []) {
  const categoryById = new Map(
    categories.map((category) => [Number(category.idCategoria), category])
  );
  const assignedGroupTaskById = new Map(
    assignedGroupTasks
      .filter((task) => task.idTarea != null)
      .map((task) => [Number(task.idTarea), task])
  );

  return tasks.map((task) => {
    const assignedGroupTask = assignedGroupTaskById.get(Number(task.idTarea));
    if (assignedGroupTask) {
      return mapAssignedGroupTaskResponseToCardTask(
        {
          ...task,
          ...assignedGroupTask,
        },
        categoryById
      );
    }

    return mapTaskResponseToCardTask(task, categoryById);
  });
}

export function mapRecommendedTaskResponseToCardTask(task, categoryById = new Map()) {
  const category = task.idCategoria ? categoryById.get(Number(task.idCategoria)) : null;
  const categoryName = task.nombreCategoria || task.categoriaNombre || category?.nombre;

  return {
    idTarea: task.idTarea,
    titulo: task.titulo,
    descripcion: task.descripcion,
    tiempo: task.tiempo,
    prioridad: task.prioridad,
    fechaEntrega: task.fechaEntrega,
    fechaAgregado: task.fechaAgregado,
    completada: task.completada,
    fechaCompletada: task.fechaCompletada,
    estado: task.estado,
    idCategoria: task.idCategoria,
    categoriaNombre: categoryName,
    nombreCategoria: categoryName,
    colorCategoria: task.colorCategoria || task.categoriaColor || category?.color || null,
    iconoCategoria: task.iconoCategoria || task.categoriaIcono || category?.icono || null,
    idUsuario: task.idUsuario,
    emailUsuarioQueCompleta: task.emailUsuarioQueCompleta,
    recordatorioInteligenteActivo:
      task.recordatorioInteligenteActivo === true || task.recordatorioActivo === true,
    origenTarea: task.origenTarea || "PERSONAL",
    idGrupoOrigen: task.idGrupoOrigen || null,
    nombreGrupoOrigen: task.nombreGrupoOrigen || null,
    estadoRevisionAsignacion: task.estadoRevisionAsignacion || null,
    comentarioRevision: task.comentarioRevision || null,
    tipoAsignacion: task.tipoAsignacion || null,
  };
}

export function mapRecommendedTaskResponsesToCardTasks(tasks, categories = []) {
  const categoryById = new Map(
    categories.map((category) => [Number(category.idCategoria), category])
  );

  return tasks.map((task) => mapRecommendedTaskResponseToCardTask(task, categoryById));
}
