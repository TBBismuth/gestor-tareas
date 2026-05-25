import apiClient from "../../../api/client.js";

export async function getMyTasks() {
  const { data } = await apiClient.get("/tarea");
  return Array.isArray(data) ? data : [];
}

export async function getRecommendedTasks() {
  const { data } = await apiClient.post("/tarea/recomendadas", {});
  return Array.isArray(data) ? data : [];
}

export async function filterCombinedTasks(filters) {
  const { data } = await apiClient.post("/tarea/filtrar-combinado", filters ?? {});
  return Array.isArray(data) ? data : [];
}

export async function getSavedAdvancedFilter() {
  const { data } = await apiClient.get("/tarea/filtro-combinado/save");
  return data ?? null;
}

export async function saveAdvancedFilter(filter) {
  const { data } = await apiClient.put("/tarea/filtro-combinado/save", filter ?? {});
  return data ?? null;
}

export async function getAssignedGroupTasks(groupId) {
  const endpoint = groupId ? `/tarea/asignadas-grupo/${groupId}` : "/tarea/asignadas-grupo";
  const { data } = await apiClient.get(endpoint);
  return Array.isArray(data) ? data : [];
}

export async function completeTask(taskId) {
  const { data } = await apiClient.patch(`/tarea/completar/${taskId}`);
  return data;
}

export async function createTask(payload) {
  const { data } = await apiClient.post("/tarea/add", payload);
  return data;
}

export async function updateTask(id, payload) {
  const { data } = await apiClient.put(`/tarea/update/${id}`, payload);
  return data;
}

export async function deleteTask(id) {
  await apiClient.delete(`/tarea/delete/${id}`);
}
