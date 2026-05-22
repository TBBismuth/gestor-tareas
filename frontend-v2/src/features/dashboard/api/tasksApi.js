import apiClient from "../../../api/client.js";

export async function getMyTasks() {
  const { data } = await apiClient.get("/tarea");
  return Array.isArray(data) ? data : [];
}

export async function completeTask(taskId) {
  const { data } = await apiClient.patch(`/tarea/completar/${taskId}`);
  return data;
}
