import api from "./api";

// Lista todas las tareas del usuario logueado
export function getTareas() {
    return api.get("/tarea"); // backend: GET /api/tarea (baseURL = "/api")
}
