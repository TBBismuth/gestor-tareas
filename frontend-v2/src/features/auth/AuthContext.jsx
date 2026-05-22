import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import {
  getCurrentUser,
  login as loginRequest,
  refresh as refreshRequest,
  register as registerRequest,
} from "./authApi.js";
import {
  clearSession,
  getAccessToken,
  getRefreshToken,
  getStoredUser,
  saveAccessToken,
  saveSession,
} from "./authStorage.js";

const AuthContext = createContext(null);

function buildSession(authPayload) {
  const accessToken = authPayload.accessToken || authPayload.raw?.token || null;
  const refreshToken = authPayload.refreshToken || null;
  const user = authPayload.user || null;

  if (accessToken || refreshToken) {
    saveSession({ accessToken, refreshToken, user });
  }

  return { accessToken, refreshToken, user };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredUser());
  const [isLoading, setIsLoading] = useState(true);
  const [sessionVersion, setSessionVersion] = useState(0);

  const logout = useCallback(() => {
    clearSession();
    setUser(null);
    setSessionVersion((current) => current + 1);
  }, []);

  const loadCurrentUser = useCallback(async () => {
    if (!getAccessToken()) {
      setUser(null);
      setIsLoading(false);
      return null;
    }

    setIsLoading(true);
    try {
      const currentUser = await getCurrentUser();
      saveSession({ user: currentUser });
      setUser(currentUser);
      return currentUser;
    } catch {
      clearSession();
      setUser(null);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCurrentUser();
  }, [loadCurrentUser, sessionVersion]);

  useEffect(() => {
    function handleSessionExpired() {
      clearSession();
      setUser(null);
      setSessionVersion((current) => current + 1);
      toast.error("Tu sesion ha caducado. Inicia sesion de nuevo.");
    }

    window.addEventListener("auth:session-expired", handleSessionExpired);
    return () => window.removeEventListener("auth:session-expired", handleSessionExpired);
  }, []);

  const login = useCallback(async (credentials) => {
    const authPayload = await loginRequest(credentials);
    const session = buildSession(authPayload);
    setUser(session.user);
    return session;
  }, []);

  const register = useCallback(async (payload) => {
    const authPayload = await registerRequest(payload);
    const session = buildSession(authPayload);

    if (session.accessToken) {
      setUser(session.user);
    }

    return session;
  }, []);

  const refreshSession = useCallback(async () => {
    const refreshToken = getRefreshToken();

    if (!refreshToken) {
      return null;
    }

    const response = await refreshRequest(refreshToken);
    const accessToken = response?.accessToken || response?.token || null;

    if (accessToken) {
      saveAccessToken(accessToken);
    }

    return accessToken;
  }, []);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(getAccessToken()),
      isLoading,
      login,
      register,
      logout,
      refreshSession,
      loadCurrentUser,
    }),
    [isLoading, loadCurrentUser, login, logout, refreshSession, register, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth debe usarse dentro de AuthProvider.");
  }

  return context;
}
