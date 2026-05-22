import apiClient from "../../api/client.js";

export function extractAuthPayload(response) {
  const data = response ?? {};
  const accessToken = data.accessToken || data.token || null;
  const refreshToken = data.refreshToken || null;
  const user =
    data.idUsuario || data.nombre || data.email
      ? {
          idUsuario: data.idUsuario,
          nombre: data.nombre,
          email: data.email,
          fechaRegistro: data.fechaRegistro,
          activo: data.activo,
          verificado: data.verificado,
        }
      : null;

  return {
    accessToken,
    refreshToken,
    user,
    raw: data,
  };
}

export async function login(credentials) {
  const { data } = await apiClient.post("/usuario/login", {
    email: credentials.email,
    password: credentials.password,
  });

  return extractAuthPayload(data);
}

export async function register(payload) {
  const { data } = await apiClient.post("/usuario/add", {
    nombre: payload.nombre,
    email: payload.email,
    password: payload.password,
  });

  return extractAuthPayload(data);
}

export async function refresh(refreshToken) {
  const { data } = await apiClient.post("/usuario/refresh", { refreshToken });
  return data;
}

export async function getCurrentUser() {
  const { data } = await apiClient.get("/usuario/me");
  return data;
}
