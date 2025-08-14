import axios from "axios";
import { getToken } from "./authService";

const api = axios.create({
    baseURL: "/api",
    headers: { "Content-Type": "application/json" },
});

// Interceptor para añadir Authorization a cada request
api.interceptors.request.use((config) => {
    // No añadir token si es la ruta de login
    if (config.url && config.url.includes("/usuario/login")) {
        return config;
    }

    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});


export default api;
