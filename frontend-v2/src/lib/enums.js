export const PRIORITIES = ["BAJA", "MEDIA", "ALTA", "IMPRESCINDIBLE"];

export const TASK_STATES = [
  "EN_CURSO",
  "COMPLETADA",
  "COMPLETADA_CON_RETRASO",
  "VENCIDA",
  "SIN_FECHA",
];

export function enumLabel(value) {
  if (!value) return "";

  return String(value)
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/^\w/, (match) => match.toUpperCase());
}
