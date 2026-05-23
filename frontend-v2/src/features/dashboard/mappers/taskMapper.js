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
