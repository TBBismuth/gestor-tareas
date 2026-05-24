function normalizeSearchText(value) {
  return String(value ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

function getTaskSearchText(task) {
  return [
    task.titulo,
    task.descripcion,
    task.categoriaNombre,
    task.nombreCategoria,
    task.nombreGrupoOrigen,
    task.estado,
    task.prioridad,
    task.tipoAsignacion,
    task.estadoRevisionAsignacion,
  ]
    .map(normalizeSearchText)
    .filter(Boolean)
    .join(" ");
}

export function filterTasksByQuickSearch(tasks, searchTerm) {
  const normalizedSearch = normalizeSearchText(searchTerm);

  if (!normalizedSearch) {
    return tasks;
  }

  return tasks.filter((task) => getTaskSearchText(task).includes(normalizedSearch));
}
