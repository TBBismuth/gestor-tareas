import apiClient from "../../../api/client.js";

export async function getMyTasks() {
  const { data } = await apiClient.get("/tarea");
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
