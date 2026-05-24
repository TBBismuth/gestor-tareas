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

let refreshPromise = null;
let sessionExpiredNotified = false;

function isAuthEndpoint(url = "") {
  return ["/usuario/login", "/usuario/add", "/usuario/refresh"].some((endpoint) =>
    url.includes(endpoint)
  );
}

function notifySessionExpired() {
  clearSession();

  if (!sessionExpiredNotified) {
    sessionExpiredNotified = true;
    window.dispatchEvent(new Event("auth:session-expired"));
  }
}

async function refreshAccessToken(refreshToken) {
  if (!refreshPromise) {
    refreshPromise = apiClient
      .post("/usuario/refresh", { refreshToken })
      .then(({ data }) => {
        const accessToken = data?.accessToken || data?.token;

        if (!accessToken) {
          throw new Error("No access token returned by refresh endpoint.");
        }

        saveAccessToken(accessToken);
        sessionExpiredNotified = false;
        return accessToken;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}

apiClient.interceptors.request.use((config) => {
  const accessToken = getAccessToken();

  if (config.url?.includes("/usuario/login") || config.url?.includes("/usuario/add")) {
    sessionExpiredNotified = false;
  }

  if (accessToken && !isAuthEndpoint(config.url)) {
    config.headers = config.headers || {};
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
      notifySessionExpired();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      const accessToken = await refreshAccessToken(refreshToken);
      originalRequest.headers = originalRequest.headers || {};
      originalRequest.headers.Authorization = `Bearer ${accessToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      notifySessionExpired();
      return Promise.reject(refreshError);
    }
  }
);

export default apiClient;
