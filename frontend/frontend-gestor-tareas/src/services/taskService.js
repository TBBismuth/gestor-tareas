// src/services/taskService.js
import apiClient from "./apiClient";

// LISTAR tareas del usuario autenticado
export function getTareas() {
    // baseURL ya es "/api" -> aquí NO ponemos "/api"
    return apiClient.get("/tarea");
}

// AÑADIR nueva tarea
export function addTarea(payload) {
    // POST /api/tarea/add (via proxy) -> ruta corta
    return apiClient.post("/tarea/add", payload);
}

// (opcional según lo que uses más adelante)
export function completarTarea(id) {
    return apiClient.patch(`/tarea/completar/${id}`);
}
export function deleteTarea(id) {
    return apiClient.delete(`/tarea/delete/${id}`);
}
export function updateTarea(id, payload) {
    return apiClient.put(`/tarea/update/${id}`, payload);
}
