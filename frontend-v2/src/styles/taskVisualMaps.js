export const priorityVisualMap = {
  BAJA: {
    label: "Baja",
    color: "var(--priority-baja)",
  },
  MEDIA: {
    label: "Media",
    color: "var(--priority-media)",
  },
  ALTA: {
    label: "Alta",
    color: "var(--priority-alta)",
  },
  IMPRESCINDIBLE: {
    label: "Imprescindible",
    color: "var(--priority-imprescindible)",
  },
  DEFAULT: {
    label: "Sin prioridad",
    color: "var(--priority-default)",
  },
};

export const stateVisualMap = {
  COMPLETADA: {
    label: "Completada",
    color: "var(--status-completada)",
  },
  EN_CURSO: {
    label: "En curso",
    color: "var(--status-en-curso)",
  },
  VENCIDA: {
    label: "Vencida",
    color: "var(--status-vencida)",
  },
  SIN_FECHA: {
    label: "Sin fecha",
    color: "var(--status-sin-fecha)",
  },
  COMPLETADA_CON_RETRASO: {
    label: "Completada con retraso",
    color: "var(--status-completada-con-retraso)",
  },
  DEFAULT: {
    label: "Sin estado",
    color: "var(--status-default)",
  },
};

export function getPriorityVisual(priority) {
  return priorityVisualMap[priority] || priorityVisualMap.DEFAULT;
}

export function getStateVisual(state) {
  return stateVisualMap[state] || stateVisualMap.DEFAULT;
}
