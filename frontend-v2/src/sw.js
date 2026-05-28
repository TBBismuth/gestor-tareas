import { clientsClaim } from "workbox-core";
import { cleanupOutdatedCaches, precacheAndRoute } from "workbox-precaching";
import { registerRoute } from "workbox-routing";
import { NetworkOnly } from "workbox-strategies";

self.skipWaiting();
clientsClaim();

precacheAndRoute(self.__WB_MANIFEST);
cleanupOutdatedCaches();

registerRoute(({ url }) => url.pathname.startsWith("/api"), new NetworkOnly());

function normalizePushPayload(event) {
  if (!event.data) {
    return {};
  }

  try {
    return event.data.json();
  } catch {
    return {
      body: event.data.text(),
    };
  }
}

self.addEventListener("push", (event) => {
  const payload = normalizePushPayload(event);
  const title = payload.title || "Gestor de Tareas";
  const options = {
    body: payload.body || "Tienes una nueva notificacion.",
    icon: payload.icon || "/pwa/pwa-192.png",
    badge: payload.badge || "/pwa/pwa-192.png",
    data: {
      url: payload.url || "/app",
    },
  };

  if (payload.requireInteraction === true) {
    options.requireInteraction = true;
  }

  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  const targetUrl = new URL(event.notification.data?.url || "/app", self.location.origin).href;

  event.waitUntil(
    self.clients.matchAll({ type: "window", includeUncontrolled: true }).then((clients) => {
      const existingClient = clients.find((client) => client.url.startsWith(targetUrl));

      if (existingClient) {
        return existingClient.focus();
      }

      return self.clients.openWindow(targetUrl);
    })
  );
});
