import api from "./apiClient";

const TOKEN_KEY = "token";

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

export function isLoggedIn() {
    return !!getToken();
}

// ---- NEW: Auth API helpers ----
export async function loginUser({ email, password }) {
    const { data } = await api.post("/usuario/login", { email, password });
    return data; // { token, ... }
}

export async function registerUser({ nombre, email, password }) {
    const { data } = await api.post("/usuario/add", { nombre, email, password });
    return data;
}
