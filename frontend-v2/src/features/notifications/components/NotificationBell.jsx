import { useEffect, useMemo, useRef, useState } from "react";
import {
  Bell,
  BellRing,
  CheckCheck,
  ChevronDown,
  Loader2,
  Settings2,
  Smartphone,
  X,
} from "lucide-react";
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
  deletePushSubscription,
  registerPushSubscription,
  updateNotificationPreferences,
} from "../api/notificationsApi.js";
import {
  buildPushSubscriptionPayload,
  createPushSubscription,
  getCurrentPushSubscription,
  getNotificationPermission,
  getPushAvailability,
} from "../lib/webPush.js";

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

function PreferenceSwitch({ checked, disabled = false, label, onChange }) {
  return (
    <label className="flex items-center justify-between gap-3 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-2 text-sm">
      <span className="font-semibold text-primary">{label}</span>
      <input
        checked={checked}
        className="peer sr-only"
        disabled={disabled}
        onChange={(event) => onChange(event.target.checked)}
        type="checkbox"
      />
      <span
        className={cn(
          "relative h-6 w-11 shrink-0 rounded-full border transition",
          checked
            ? "border-[color:var(--color-brand)] bg-[color:var(--color-brand)]"
            : "border-app bg-[color:var(--state-inactive-bg)]",
          disabled && "opacity-55"
        )}
        aria-hidden="true"
      >
        <span
          className={cn(
            "absolute left-0.5 top-0.5 size-5 rounded-full bg-white shadow-control transition",
            checked && "translate-x-5"
          )}
        />
      </span>
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

function GroupChip({ active, children, onClick }) {
  return (
    <button
      type="button"
      aria-pressed={active}
      className={cn(
        "inline-flex max-w-full items-center gap-1.5 rounded-control border px-2.5 py-1.5 text-xs font-semibold transition",
        active
          ? "border-[color:var(--color-brand)] bg-[color:var(--state-active-bg)] text-[color:var(--state-active-text)]"
          : "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]"
      )}
      onClick={onClick}
    >
      <span className="min-w-0 truncate">{children}</span>
    </button>
  );
}

function PushDeviceControls() {
  const [permission, setPermission] = useState(() => getNotificationPermission());
  const [subscribed, setSubscribed] = useState(false);
  const [checking, setChecking] = useState(true);
  const availability = getPushAvailability();

  useEffect(() => {
    let cancelled = false;

    async function loadSubscriptionState() {
      if (!availability.available) {
        setChecking(false);
        return;
      }

      try {
        const subscription = await getCurrentPushSubscription();
        if (!cancelled) {
          setSubscribed(Boolean(subscription));
          setPermission(getNotificationPermission());
        }
      } catch {
        if (!cancelled) {
          setSubscribed(false);
        }
      } finally {
        if (!cancelled) {
          setChecking(false);
        }
      }
    }

    loadSubscriptionState();

    return () => {
      cancelled = true;
    };
  }, [availability.available]);

  const activateMutation = useMutation({
    mutationFn: async () => {
      const subscription = await createPushSubscription();
      const payload = buildPushSubscriptionPayload(subscription);
      try {
        await registerPushSubscription(payload);
      } catch (error) {
        await subscription.unsubscribe().catch(() => {});
        throw error;
      }
      return subscription;
    },
    onSuccess: () => {
      setSubscribed(true);
      setPermission(getNotificationPermission());
      toast.success("Notificaciones del dispositivo activadas.");
    },
    onError: (error) => {
      const message = error?.message || "";
      if (message.includes("denegado")) {
        toast.error("Permiso de notificaciones denegado.");
        setPermission(getNotificationPermission());
        return;
      }
      if (message.includes("soporta")) {
        toast.error("Este navegador no soporta notificaciones push.");
        return;
      }
      toast.error("No se pudieron activar las notificaciones del dispositivo.");
    },
  });
  const deactivateMutation = useMutation({
    mutationFn: async () => {
      const subscription = await getCurrentPushSubscription();

      if (!subscription) {
        return;
      }

      await deletePushSubscription(subscription.endpoint);
      await subscription.unsubscribe();
    },
    onSuccess: () => {
      setSubscribed(false);
      setPermission(getNotificationPermission());
      toast.success("Notificaciones del dispositivo desactivadas.");
    },
    onError: () => {
      toast.error("No se pudieron desactivar las notificaciones del dispositivo.");
    },
  });

  return (
    <div className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] p-3">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="flex items-center gap-2 text-sm font-semibold text-primary">
            <Smartphone size={16} />
            Notificaciones del dispositivo
          </p>
          <p className="mt-1 text-xs leading-5 text-muted">
            {availability.available
              ? subscribed
                ? "Este navegador esta suscrito."
                : permission === "denied"
                  ? "El permiso esta denegado en el navegador."
                  : "Activa avisos del sistema en este navegador."
              : availability.reason}
          </p>
        </div>
        {checking && <Loader2 className="shrink-0 animate-spin text-muted" size={16} />}
      </div>

      <div className="mt-3 flex flex-wrap gap-2">
        <Button
          disabled={
            !availability.available ||
            permission === "denied" ||
            subscribed ||
            checking ||
            activateMutation.isPending
          }
          onClick={() => activateMutation.mutate()}
          size="sm"
          variant="secondary"
        >
          <BellRing size={15} />
          Activar
        </Button>
        <Button
          disabled={!subscribed || checking || deactivateMutation.isPending}
          onClick={() => deactivateMutation.mutate()}
          size="sm"
          variant="secondary"
        >
          <X size={15} />
          Desactivar
        </Button>
      </div>
    </div>
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

  function toggleGroup(groupId) {
    const normalizedGroupId = Number(groupId);
    const selectedGroups = preferences.gruposAviso24h.map(Number);
    const nextGroups = selectedGroups.includes(normalizedGroupId)
      ? selectedGroups.filter((current) => current !== normalizedGroupId)
      : [...selectedGroups, normalizedGroupId];

    updatePreferences({ gruposAviso24h: nextGroups });
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
      <PushDeviceControls />

      <PreferenceSwitch
        checked={preferences.notificacionesActivas}
        label="Notificaciones internas"
        disabled={updatePreferencesMutation.isPending}
        onChange={(notificacionesActivas) => updatePreferences({ notificacionesActivas })}
      />
      <PreferenceSwitch
        checked={preferences.aviso24hActivo}
        label="Aviso 24h antes"
        disabled={updatePreferencesMutation.isPending}
        onChange={(aviso24hActivo) => updatePreferences({ aviso24hActivo })}
      />
      <PreferenceSwitch
        checked={preferences.recordatorioInteligenteActivo}
        label="Recordatorio inteligente"
        disabled={updatePreferencesMutation.isPending}
        onChange={(recordatorioInteligenteActivo) =>
          updatePreferences({ recordatorioInteligenteActivo })
        }
      />
      <PreferenceSwitch
        checked={preferences.notificarAsignacionesGrupoActivo}
        label="Asignaciones de grupo"
        disabled={updatePreferencesMutation.isPending}
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

      <div>
        <p className="mb-2 text-xs font-semibold uppercase text-muted">Grupos aviso 24h</p>
        {groups.length > 0 ? (
          <div className="flex flex-wrap gap-2">
            {groups.map((group) => (
              <GroupChip
                active={preferences.gruposAviso24h.map(Number).includes(Number(group.idGrupo))}
                key={group.idGrupo}
                onClick={() => toggleGroup(group.idGrupo)}
              >
                {group.nombre}
              </GroupChip>
            ))}
          </div>
        ) : (
          <p className="rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-3 py-2 text-xs text-muted">
            No hay grupos disponibles para configurar avisos 24h.
          </p>
        )}
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
