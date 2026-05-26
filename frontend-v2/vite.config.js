import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    VitePWA({
      registerType: "autoUpdate",
      includeAssets: ["favicon.svg", "pwa/apple-touch-icon.png"],
      manifest: {
        name: "Gestor de Tareas",
        short_name: "Gestor",
        description: "Gestor de tareas personal y grupal",
        theme_color: "#60A5FA",
        background_color: "#0B1120",
        display: "standalone",
        lang: "es",
        scope: "/",
        start_url: "/app",
        icons: [
          {
            src: "/pwa/pwa-192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "/pwa/pwa-512.png",
            sizes: "512x512",
            type: "image/png",
          },
          {
            src: "/pwa/pwa-maskable.png",
            sizes: "512x512",
            type: "image/png",
            purpose: "maskable",
          },
          {
            src: "/pwa/apple-touch-icon.png",
            sizes: "180x180",
            type: "image/png",
            purpose: "any",
          },
        ],
        screenshots: [
          {
            src: "/pwa/mobile-home.png",
            sizes: "1080x2400",
            type: "image/png",
            form_factor: "narrow",
            label: "Inicio movil",
          },
          {
            src: "/pwa/mobile-category.png",
            sizes: "1080x2400",
            type: "image/png",
            form_factor: "narrow",
            label: "Categorias movil",
          },
          {
            src: "/pwa/desktop-home.png",
            sizes: "1919x1079",
            type: "image/png",
            form_factor: "wide",
            label: "Inicio escritorio",
          },
          {
            src: "/pwa/desktop-category.png",
            sizes: "1919x1079",
            type: "image/png",
            form_factor: "wide",
            label: "Categorias escritorio",
          },
        ],
      },
      workbox: {
        runtimeCaching: [
          {
            urlPattern: ({ url }) => url.pathname.startsWith("/api"),
            handler: "NetworkOnly",
          },
        ],
        navigateFallbackDenylist: [/^\/api(?:\/|$)/],
      },
    }),
  ],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
