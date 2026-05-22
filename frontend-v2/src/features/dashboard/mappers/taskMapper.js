export function mapTaskResponseToCardTask(task) {
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
    categoriaNombre: task.categoriaNombre,
    nombreCategoria: task.categoriaNombre,
    colorCategoria: null,
    iconoCategoria: null,
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

export function mapTaskResponsesToCardTasks(tasks) {
  return tasks.map(mapTaskResponseToCardTask);
}
