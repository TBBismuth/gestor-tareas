// src/services/taskService.js
import apiClient from "./apiClient";
import api from "./apiClient";

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

export async function getTareasOrdenadas(criterio) {
    let url = `/tarea`;

    switch (criterio) {
        case "titulo":
            url += "/titulo";
            break;
        case "tiempo":
            url += "/tiempo";
            break;
        case "prioridad":
            url += "/prioridad";
            break;
        case "hoy":
            url += "/hoy";
            break;
        default:
            break;
    }

    const response = await api.get(url);
    return response.data;
}

export async function filtrarTareas(tipo, valor) {
    let url = "";
    console.log("Token usado:", localStorage.getItem("token"));

    switch (tipo) {
        case "prioridad":
            url = `/tarea/filtrar/prioridad/${valor}`;
            break;
        case "estado":
            url = `/tarea/filtrar/estado/${valor}`;
            break;
        case "categoria":
            url = `/tarea/filtrar/categoria/${valor}`;
            break;
        case "tiempo":
            url = `/tarea/filtrar/tiempo/${valor}`;
            break;
        case "palabras":
            url = `/tarea/filtrar/palabras/${valor}`;
            break;
        default:
            throw new Error("Filtro no válido");
    }

    const response = await api.get(url);
    return response.data;
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
