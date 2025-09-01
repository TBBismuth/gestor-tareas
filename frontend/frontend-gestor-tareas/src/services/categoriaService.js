import apiClient from "./apiClient";

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

export async function searchCategoriasByName(nombreParcial) {
    const { data } = await apiClient.get(`/categoria/nombre/${encodeURIComponent(nombreParcial)}`);
    return data;
}