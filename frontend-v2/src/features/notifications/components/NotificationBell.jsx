import { useEffect, useMemo, useRef, useState } from "react";
import { Bell, CheckCheck, ChevronDown, Loader2, Settings2, X } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import Button from "../../../components/ui/Button.jsx";
import IconButton from "../../../components/ui/IconButton.jsx";
import { cn } from "../../../lib/cn.js";
import { enumLabel, PRIORITIES } from "../../../lib/enums.js";
import { formatShortDate } from "../../../lib/dates.js";
import {
  closeAllNotifications,
  closeNotification,
  getNotifications,
  getNotificationsCount,
  getNotificationPreferences,
  updateNotificationPreferences,
} from "../api/notificationsApi.js";

export const NOTIFICATIONS_QUERY_KEY = ["notifications", "active"];
export const NOTIFICATIONS_COUNT_QUERY_KEY = ["notifications", "count"];
export const NOTIFICATION_PREFERENCES_QUERY_KEY = ["notifications", "preferences"];

const defaultPreferences = {
  notificacionesActivas: true,
  aviso24hActivo: true,
  prioridadesAviso24h: [],
  gruposAviso24h: [],
  recordatorioInteligenteActivo: false,
  notificarAsignacionesGrupoActivo: true,
};

function formatNotificationDate(value) {
  if (!value) return null;

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return formatShortDate(value);
  }

  return new Intl.DateTimeFormat("es-ES", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function normalizePreferences(preferences) {
  return {
    ...defaultPreferences,
    ...(preferences ?? {}),
    prioridadesAviso24h: Array.isArray(preferences?.prioridadesAviso24h)
      ? preferences.prioridadesAviso24h
      : [],
    gruposAviso24h: Array.isArray(preferences?.gruposAviso24h)
      ? preferences.gruposAviso24h
      : [],
  };
}

function buildPreferencesPayload(preferences, patch = {}) {
  const nextPreferences = {
    ...preferences,
    ...patch,
  };

  return {
    notificacionesActivas: nextPreferences.notificacionesActivas,
    aviso24hActivo: nextPreferences.aviso24hActivo,
    prioridadesAviso24h: nextPreferences.prioridadesAviso24h,
    gruposAviso24h: nextPreferences.gruposAviso24h,
    recordatorioInteligenteActivo: nextPreferences.recordatorioInteligenteActivo,
    notificarAsignacionesGrupoActivo: nextPreferences.notificarAsignacionesGrupoActivo,
  };
}

function PreferenceToggle({ checked, label, onChange }) {
  return (
    <label className="flex items-center justify-between gap-3 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-2 text-sm">
      <span className="font-semibold text-primary">{label}</span>
      <input
        checked={checked}
        className="size-4 accent-[color:var(--color-brand)]"
        onChange={(event) => onChange(event.target.checked)}
        type="checkbox"
      />
    </label>
  );
}

function PriorityChip({ active, children, onClick }) {
  return (
    <button
      type="button"
      aria-pressed={active}
      className={cn(
        "rounded-control border px-2.5 py-1.5 text-xs font-semibold transition",
        active
          ? "border-[color:var(--color-brand)] bg-[color:var(--state-active-bg)] text-[color:var(--state-active-text)]"
          : "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]"
      )}
      onClick={onClick}
    >
      {children}
    </button>
  );
}

function NotificationPreferences({ groups = [] }) {
  const queryClient = useQueryClient();
  const preferencesQuery = useQuery({
    queryKey: NOTIFICATION_PREFERENCES_QUERY_KEY,
    queryFn: getNotificationPreferences,
  });
  const preferences = normalizePreferences(preferencesQuery.data);
  const updatePreferencesMutation = useMutation({
    mutationFn: updateNotificationPreferences,
    onSuccess: (updatedPreferences) => {
      queryClient.setQueryData(NOTIFICATION_PREFERENCES_QUERY_KEY, updatedPreferences);
      toast.success("Preferencias actualizadas.");
    },
    onError: () => {
      toast.error("No se pudieron actualizar las preferencias.");
    },
  });

  function updatePreferences(patch) {
    updatePreferencesMutation.mutate(buildPreferencesPayload(preferences, patch));
  }

  function togglePriority(priority) {
    const nextPriorities = preferences.prioridadesAviso24h.includes(priority)
      ? preferences.prioridadesAviso24h.filter((current) => current !== priority)
      : [...preferences.prioridadesAviso24h, priority];

    updatePreferences({ prioridadesAviso24h: nextPriorities });
  }

  if (preferencesQuery.isLoading) {
    return <p className="text-sm text-secondary">Cargando preferencias...</p>;
  }

  if (preferencesQuery.isError) {
    return (
      <p className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-3 py-2 text-sm text-[color:var(--state-danger-text)]">
        No se pudieron cargar las preferencias.
      </p>
    );
  }

  return (
    <div className="grid gap-3">
      <PreferenceToggle
        checked={preferences.notificacionesActivas}
        label="Notificaciones internas"
        onChange={(notificacionesActivas) => updatePreferences({ notificacionesActivas })}
      />
      <PreferenceToggle
        checked={preferences.aviso24hActivo}
        label="Aviso 24h antes"
        onChange={(aviso24hActivo) => updatePreferences({ aviso24hActivo })}
      />
      <PreferenceToggle
        checked={preferences.recordatorioInteligenteActivo}
        label="Recordatorio inteligente"
        onChange={(recordatorioInteligenteActivo) =>
          updatePreferences({ recordatorioInteligenteActivo })
        }
      />
      <PreferenceToggle
        checked={preferences.notificarAsignacionesGrupoActivo}
        label="Asignaciones de grupo"
        onChange={(notificarAsignacionesGrupoActivo) =>
          updatePreferences({ notificarAsignacionesGrupoActivo })
        }
      />

      <div>
        <p className="mb-2 text-xs font-semibold uppercase text-muted">Prioridades aviso 24h</p>
        <div className="flex flex-wrap gap-2">
          {PRIORITIES.map((priority) => (
            <PriorityChip
              active={preferences.prioridadesAviso24h.includes(priority)}
              key={priority}
              onClick={() => togglePriority(priority)}
            >
              {enumLabel(priority)}
            </PriorityChip>
          ))}
        </div>
      </div>

      <div className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-2 text-xs text-muted">
        {groups.length > 0
          ? "La seleccion de grupos para aviso 24h queda preparada para Mi perfil."
          : "Los grupos para aviso 24h quedan pendientes para el bloque de Mi perfil."}
      </div>

      {updatePreferencesMutation.isPending && (
        <p className="text-xs text-muted">Guardando preferencias...</p>
      )}
    </div>
  );
}

export default function NotificationBell({ groups = [] }) {
  const [open, setOpen] = useState(false);
  const [preferencesOpen, setPreferencesOpen] = useState(false);
  const panelRef = useRef(null);
  const queryClient = useQueryClient();
  const notificationsQuery = useQuery({
    queryKey: NOTIFICATIONS_QUERY_KEY,
    queryFn: getNotifications,
    enabled: open,
  });
  const countQuery = useQuery({
    queryKey: NOTIFICATIONS_COUNT_QUERY_KEY,
    queryFn: getNotificationsCount,
    refetchInterval: 60000,
  });
  const notifications = notificationsQuery.data ?? [];
  const count = Number(countQuery.data ?? notifications.length ?? 0);
  const visibleCount = count > 99 ? "99+" : String(count);
  const hasNotifications = count > 0 || notifications.length > 0;
  const sortedNotifications = useMemo(
    () =>
      [...notifications].sort(
        (a, b) => new Date(b.fechaCreacion ?? 0).getTime() - new Date(a.fechaCreacion ?? 0).getTime()
      ),
    [notifications]
  );

  const closeNotificationMutation = useMutation({
    mutationFn: closeNotification,
    onSuccess: () => {
      toast.success("Notificacion cerrada.");
      queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_COUNT_QUERY_KEY });
    },
    onError: () => {
      toast.error("No se pudo cerrar la notificacion.");
    },
  });
  const closeAllMutation = useMutation({
    mutationFn: closeAllNotifications,
    onSuccess: () => {
      toast.success("Notificaciones cerradas.");
      queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_COUNT_QUERY_KEY });
    },
    onError: () => {
      toast.error("No se pudieron cerrar las notificaciones.");
    },
  });

  useEffect(() => {
    if (!open) return undefined;

    function handlePointerDown(event) {
      if (!panelRef.current?.contains(event.target)) {
        setOpen(false);
      }
    }

    function handleKeyDown(event) {
      if (event.key === "Escape") {
        setOpen(false);
      }
    }

    document.addEventListener("pointerdown", handlePointerDown);
    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("pointerdown", handlePointerDown);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [open]);

  return (
    <div className="relative" ref={panelRef}>
      <IconButton
        className="relative"
        label="Abrir notificaciones"
        onClick={() => setOpen((current) => !current)}
      >
        <Bell size={18} />
        {count > 0 && (
          <span className="absolute -right-1 -top-1 grid min-w-5 place-items-center rounded-full border border-[color:var(--color-surface-panel)] bg-[color:var(--state-danger-text)] px-1 text-[0.68rem] font-bold leading-5 text-white">
            {visibleCount}
          </span>
        )}
      </IconButton>

      {open && (
        <section className="absolute right-0 top-full z-50 mt-2 w-[min(22rem,calc(100vw-2rem))] overflow-hidden rounded-panel border border-app bg-panel shadow-panel">
          <header className="flex items-center justify-between gap-3 border-b border-app px-4 py-3">
            <div>
              <h2 className="text-base font-semibold text-primary">Notificaciones</h2>
              <p className="text-xs text-muted">{count} activas</p>
            </div>
            {hasNotifications && (
              <Button
                disabled={closeAllMutation.isPending}
                onClick={() => closeAllMutation.mutate()}
                size="sm"
                variant="secondary"
              >
                <CheckCheck size={15} />
                Cerrar todas
              </Button>
            )}
          </header>

          <div className="max-h-[24rem] overflow-y-auto p-3">
            {notificationsQuery.isLoading && (
              <p className="flex items-center gap-2 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-3 text-sm text-secondary">
                <Loader2 className="animate-spin" size={16} />
                Cargando notificaciones...
              </p>
            )}
            {notificationsQuery.isError && (
              <p className="rounded-control border border-app bg-[color:var(--state-danger-bg)] px-3 py-3 text-sm text-[color:var(--state-danger-text)]">
                No se pudieron cargar las notificaciones.
              </p>
            )}
            {notificationsQuery.isSuccess && sortedNotifications.length === 0 && (
              <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-4 text-sm text-secondary">
                No tienes notificaciones activas.
              </p>
            )}
            {sortedNotifications.length > 0 && (
              <div className="grid gap-2">
                {sortedNotifications.map((notification) => {
                  const createdAt = formatNotificationDate(notification.fechaCreacion);

                  return (
                    <article
                      className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] p-3"
                      key={notification.idNotificacion}
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <p className="font-semibold text-primary">
                            {notification.titulo || "Notificacion"}
                          </p>
                          <p className="mt-1 text-sm leading-5 text-secondary">
                            {notification.mensaje}
                          </p>
                        </div>
                        <IconButton
                          className="danger-icon-button size-8 shrink-0"
                          disabled={closeNotificationMutation.isPending}
                          label="Cerrar notificacion"
                          onClick={() =>
                            closeNotificationMutation.mutate(notification.idNotificacion)
                          }
                        >
                          <X size={15} />
                        </IconButton>
                      </div>
                      <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-muted">
                        <span>{enumLabel(notification.tipo)}</span>
                        {createdAt && <span>{createdAt}</span>}
                      </div>
                    </article>
                  );
                })}
              </div>
            )}
          </div>

          <div className="border-t border-app p-3">
            <button
              type="button"
              className="flex w-full items-center justify-between gap-2 rounded-control px-2 py-2 text-sm font-semibold text-secondary transition hover:bg-[color:var(--state-inactive-bg)] hover:text-primary"
              aria-expanded={preferencesOpen}
              onClick={() => setPreferencesOpen((current) => !current)}
            >
              <span className="inline-flex items-center gap-2">
                <Settings2 size={16} />
                Preferencias
              </span>
              <ChevronDown
                size={16}
                className={cn("transition", preferencesOpen && "rotate-180")}
              />
            </button>
            {preferencesOpen && (
              <div className="mt-2">
                <NotificationPreferences groups={groups} />
              </div>
            )}
          </div>
        </section>
      )}
    </div>
  );
}
