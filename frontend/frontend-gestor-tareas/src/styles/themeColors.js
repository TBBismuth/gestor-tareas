// PRIORIDADES
export const PRIORIDAD_BG = {
  BAJA: "rgba(34, 197, 94, 0.75)",   // verde-500
  MEDIA: "rgba(245, 158, 11, 0.75)",  // amber-500
  ALTA: "rgba(239, 68, 68, 0.75)",   // red-500
  IMPRESCINDIBLE: "rgba(109, 40, 217, 0.75)",  // violet-700
  _DEFAULT: "rgba(156, 163, 175, 0.75)", // gray-400
};

// ESTADOS
export const ESTADO_BG = {
  COMPLETADA: "rgba(5, 150, 105, 0.75)",   // emerald-600 (verde más oscuro)
  EN_CURSO: "rgba(2, 132, 199, 0.75)",   // sky-600 (azul más oscuro)
  VENCIDA: "rgba(220, 38, 38, 0.75)",   // red-600 (rojo claro de alerta)
  COMPLETADA_CON_RETRASO: "rgba(217, 119, 6, 0.75)",   // amber-600 (aviso/retardo)
  SIN_FECHA: "rgba(107, 114, 128, 0.75)", // gray-500 
  _DEFAULT: "rgba(156, 163, 175, 0.75)",
};

export function pick(map, key) {
  if (!key) return map._DEFAULT;
  const k = String(key).toUpperCase();
  return map[k] || map._DEFAULT;
}
