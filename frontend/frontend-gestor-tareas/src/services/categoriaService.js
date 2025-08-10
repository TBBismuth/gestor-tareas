// src/services/categoriaService.js
import apiClient from "./apiClient";

/**
 * GET /api/categoria
 * Devuelve directamente el ARRAY de categorías.
 */
export async function getCategorias() {
    const { data } = await apiClient.get("/categoria");
    return data;
}

export function addCategoria(payload) {
    return apiClient.post("/categoria/add", payload);
}

export function deleteCategoria(id) {
    return apiClient.delete(`/categoria/delete/${id}`);
}

export function updateCategoria(id, payload) {
    return apiClient.put(`/categoria/update/${id}`, payload);
}
