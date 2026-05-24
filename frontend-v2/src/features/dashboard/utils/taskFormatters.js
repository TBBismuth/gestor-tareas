const DATE_WITH_TIME_FORMATTER = new Intl.DateTimeFormat("es-ES", {
  day: "numeric",
  month: "short",
  hour: "2-digit",
  minute: "2-digit",
});

const DATE_WITH_YEAR_FORMATTER = new Intl.DateTimeFormat("es-ES", {
  day: "numeric",
  month: "short",
  year: "numeric",
});

const FULL_DATE_FORMATTER = new Intl.DateTimeFormat("es-ES", {
  day: "numeric",
  month: "short",
  year: "numeric",
  hour: "2-digit",
  minute: "2-digit",
});

const groupReviewLabels = {
  PENDIENTE: "Pendiente",
  ENTREGADA: "Entregada",
  VALIDADA: "Validada",
  REABIERTA: "Reabierta",
};

const assignmentTypeLabels = {
  TODO_GRUPO: "Todo el grupo",
  SELECCION_MANUAL: "Seleccion manual",
  INDIVIDUAL: "Individual",
  GRUPAL: "Grupal",
  TODOS: "Todos los miembros",
};

function parseDate(value) {
  if (!value) return null;

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function cleanDateLabel(value) {
  return value.replace(",", "").replace(/\./g, "");
}

export function formatTaskDate(value) {
  const date = parseDate(value);

  if (!date) {
    return "Sin fecha";
  }

  const currentYear = new Date().getFullYear();

  if (date.getFullYear() !== currentYear) {
    return cleanDateLabel(DATE_WITH_YEAR_FORMATTER.format(date));
  }

  return cleanDateLabel(DATE_WITH_TIME_FORMATTER.format(date));
}

export function formatTaskFullDate(value) {
  const date = parseDate(value);

  if (!date) {
    return "Sin fecha";
  }

  return cleanDateLabel(FULL_DATE_FORMATTER.format(date));
}

export function formatDuration(minutes) {
  const value = Number(minutes);

  if (!Number.isFinite(value) || value <= 0) {
    return "Sin estimacion";
  }

  if (value < 60) {
    return `${value} min`;
  }

  const hours = Math.floor(value / 60);
  const remainingMinutes = value % 60;

  if (!remainingMinutes) {
    return `${hours} h`;
  }

  return `${hours} h ${remainingMinutes} min`;
}

export function getTaskCategoryName(task) {
  return task.nombreCategoria || task.categoriaNombre || task.categoria || "Sin categoria";
}

export function formatGroupReviewState(value) {
  return groupReviewLabels[value] || "Sin estado grupal";
}

export function formatAssignmentType(value) {
  return assignmentTypeLabels[value] || "Sin tipo";
}
