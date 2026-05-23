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
    origenTarea: "PERSONAL",
    idGrupoOrigen: null,
    nombreGrupoOrigen: null,
    estadoRevisionAsignacion: null,
    comentarioRevision: null,
    tipoAsignacion: null,
  };
}

export function mapTaskResponsesToCardTasks(tasks, categories = []) {
  const categoryById = new Map(
    categories.map((category) => [Number(category.idCategoria), category])
  );

  return tasks.map((task) => mapTaskResponseToCardTask(task, categoryById));
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
