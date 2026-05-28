import apiClient from "../../../api/client.js";

export async function getNotifications() {
  const { data } = await apiClient.get("/notificaciones");
  return Array.isArray(data) ? data : [];
}

export async function getNotificationsCount() {
  const { data } = await apiClient.get("/notificaciones/count");
  return Number(data?.count ?? 0);
}

export async function closeNotification(id) {
  const { data } = await apiClient.patch(`/notificaciones/${id}/cerrar`);
  return data;
}

export async function closeAllNotifications() {
  await apiClient.patch("/notificaciones/cerrar-todas");
}

export async function getNotificationPreferences() {
  const { data } = await apiClient.get("/notificaciones/preferencias");
  return data ?? null;
}

export async function updateNotificationPreferences(payload) {
  const { data } = await apiClient.put("/notificaciones/preferencias", payload);
  return data;
}
