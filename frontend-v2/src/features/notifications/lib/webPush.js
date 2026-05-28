import { getAccessToken } from "../../auth/authStorage.js";

const vapidPublicKey = import.meta.env.VITE_WEBPUSH_PUBLIC_KEY || "";

export function getPushAvailability() {
  if (typeof window === "undefined" || typeof navigator === "undefined") {
    return {
      available: false,
      reason: "Este navegador no soporta notificaciones push.",
    };
  }

  if (!("Notification" in window) || !("serviceWorker" in navigator) || !("PushManager" in window)) {
    return {
      available: false,
      reason: "Este navegador no soporta notificaciones push.",
    };
  }

  if (!vapidPublicKey) {
    return {
      available: false,
      reason: "Falta configurar VITE_WEBPUSH_PUBLIC_KEY.",
    };
  }

  return {
    available: true,
    reason: "",
  };
}

export function getNotificationPermission() {
  if (typeof window === "undefined" || !("Notification" in window)) {
    return "unsupported";
  }

  return Notification.permission;
}

export async function getCurrentPushSubscription() {
  const availability = getPushAvailability();
  if (!availability.available) {
    return null;
  }

  const registration = await navigator.serviceWorker.ready;
  return registration.pushManager.getSubscription();
}

export async function createPushSubscription() {
  const availability = getPushAvailability();
  if (!availability.available) {
    throw new Error(availability.reason);
  }

  if (!getAccessToken()) {
    throw new Error("No hay sesion activa.");
  }

  const permission = await Notification.requestPermission();
  if (permission !== "granted") {
    throw new Error("Permiso de notificaciones denegado.");
  }

  const registration = await navigator.serviceWorker.ready;
  const currentSubscription = await registration.pushManager.getSubscription();

  if (currentSubscription) {
    return currentSubscription;
  }

  return registration.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: urlBase64ToUint8Array(vapidPublicKey),
  });
}

export function buildPushSubscriptionPayload(subscription) {
  const serialized = subscription.toJSON();

  return {
    endpoint: serialized.endpoint,
    p256dh: serialized.keys?.p256dh,
    auth: serialized.keys?.auth,
    userAgent: navigator.userAgent,
    nombreDispositivo: getDeviceName(),
  };
}

function getDeviceName() {
  const platform = navigator.userAgentData?.platform || navigator.platform;
  return platform ? `Navegador web (${platform})` : "Navegador web";
}

function urlBase64ToUint8Array(base64String) {
  const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
  const base64 = `${base64String}${padding}`.replaceAll("-", "+").replaceAll("_", "/");
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let index = 0; index < rawData.length; index += 1) {
    outputArray[index] = rawData.charCodeAt(index);
  }

  return outputArray;
}
