import axios from "axios";
import {
  clearSession,
  getAccessToken,
  getRefreshToken,
  saveAccessToken,
} from "../features/auth/authStorage.js";

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

function isAuthEndpoint(url = "") {
  return ["/usuario/login", "/usuario/add", "/usuario/refresh"].some((endpoint) =>
    url.includes(endpoint)
  );
}

apiClient.interceptors.request.use((config) => {
  const accessToken = getAccessToken();

  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    if (
      status !== 401 ||
      !originalRequest ||
      originalRequest._retry ||
      isAuthEndpoint(originalRequest.url)
    ) {
      return Promise.reject(error);
    }

    const refreshToken = getRefreshToken();

    if (!refreshToken) {
      clearSession();
      window.dispatchEvent(new Event("auth:session-expired"));
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      const { data } = await apiClient.post("/usuario/refresh", { refreshToken });
      const accessToken = data?.accessToken || data?.token;

      if (!accessToken) {
        throw new Error("No access token returned by refresh endpoint.");
      }

      saveAccessToken(accessToken);
      originalRequest.headers = originalRequest.headers || {};
      originalRequest.headers.Authorization = `Bearer ${accessToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      clearSession();
      window.dispatchEvent(new Event("auth:session-expired"));
      return Promise.reject(refreshError);
    }
  }
);

export default apiClient;
