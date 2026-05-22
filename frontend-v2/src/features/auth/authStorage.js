import { readStorage, removeStorage, writeStorage } from "../../lib/storage.js";

export const AUTH_STORAGE_KEYS = {
  accessToken: "gestor-tareas.frontend-v2.access-token",
  refreshToken: "gestor-tareas.frontend-v2.refresh-token",
  user: "gestor-tareas.frontend-v2.user",
};

export function getAccessToken() {
  return readStorage(AUTH_STORAGE_KEYS.accessToken);
}

export function getRefreshToken() {
  return readStorage(AUTH_STORAGE_KEYS.refreshToken);
}

export function getStoredUser() {
  const rawUser = readStorage(AUTH_STORAGE_KEYS.user);

  if (!rawUser) {
    return null;
  }

  try {
    return JSON.parse(rawUser);
  } catch {
    removeStorage(AUTH_STORAGE_KEYS.user);
    return null;
  }
}

export function saveAccessToken(accessToken) {
  if (accessToken) {
    writeStorage(AUTH_STORAGE_KEYS.accessToken, accessToken);
  }
}

export function saveSession({ accessToken, refreshToken, user }) {
  if (accessToken) {
    writeStorage(AUTH_STORAGE_KEYS.accessToken, accessToken);
  }

  if (refreshToken) {
    writeStorage(AUTH_STORAGE_KEYS.refreshToken, refreshToken);
  }

  if (user) {
    writeStorage(AUTH_STORAGE_KEYS.user, JSON.stringify(user));
  }
}

export function clearSession() {
  removeStorage(AUTH_STORAGE_KEYS.accessToken);
  removeStorage(AUTH_STORAGE_KEYS.refreshToken);
  removeStorage(AUTH_STORAGE_KEYS.user);
}
