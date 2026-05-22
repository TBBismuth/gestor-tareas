const FALLBACK_ERROR = "No se ha podido completar la operacion.";

export function getAuthErrorMessage(error, fallback = FALLBACK_ERROR) {
  const data = error?.response?.data;

  if (!data) {
    return error?.message || fallback;
  }

  if (typeof data === "string") {
    return data;
  }

  if (data.error) {
    return data.error;
  }

  if (data.message) {
    return data.message;
  }

  if (data.errors && typeof data.errors === "object") {
    return Object.values(data.errors).filter(Boolean).join(" ");
  }

  return fallback;
}
