import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { VitePWA } from 'vite-plugin-pwa'

export default ({ mode }) => {
  // Carga variables de .env, .env.render, etc.
  const env = loadEnv(mode, process.cwd(), '')
  // Destino del proxy según el modo
  const target = mode === 'render'
    ? env.VITE_API_BASE_URL || 'https://tu-backend-en-render.example.com'
    : 'http://localhost:8080'

  return defineConfig({
    plugins: [
      react(),
      tailwindcss(),
      VitePWA({
        registerType: 'autoUpdate',
        devOptions: { enabled: true },
        includeAssets: ['favicon.ico', 'robots.txt', 'apple-touch-icon.png'],
        manifest: {
          name: 'Gestor de Tareas',
          short_name: 'Gestor',
          description: 'Gestor de Tareas — PWA',
          theme_color: '#0ea5e9',
          background_color: '#0b1120',
          display: 'standalone',
          scope: '/',
          start_url: '/',
          icons: [
            { src: '/icons/pwa-192.png', sizes: '192x192', type: 'image/png' },
            { src: '/icons/pwa-512.png', sizes: '512x512', type: 'image/png' },
            { src: '/icons/pwa-maskable.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' }
          ],
          screenshots: [
            { src: '/screenshots/mobile-home.png', sizes: '1080x2400', type: 'image/png', form_factor: 'narrow', label: 'Inicio — móvil' },
            { src: '/screenshots/mobile-tasks.png', sizes: '1080x2400', type: 'image/png', form_factor: 'narrow', label: 'Tareas — móvil' },
            { src: '/screenshots/desktop-home.png', sizes: '1920x1080', type: 'image/png', form_factor: 'wide', label: 'Inicio — escritorio' },
            { src: '/screenshots/desktop-tasks.png', sizes: '1920x1080', type: 'image/png', form_factor: 'wide', label: 'Tareas — escritorio' }
          ],
          shortcuts: [
            { name: 'Mis tareas', short_name: 'Tareas', description: 'Abrir la lista de tareas', url: '/home', icons: [{ src: '/icons/pwa-192.png', sizes: '192x192', type: 'image/png' }] },
            { name: 'Categorías', short_name: 'Categorías', description: 'Gestionar categorías', url: '/categories', icons: [{ src: '/icons/pwa-192.png', sizes: '192x192', type: 'image/png' }] },
            { name: 'Nueva tarea', short_name: 'Nueva', description: 'Ir a la pantalla de tareas para crear una nueva', url: '/home?newTask=1', icons: [{ src: '/icons/pwa-192.png', sizes: '192x192', type: 'image/png' }] }
          ],
        },
        workbox: {
          runtimeCaching: [
            {
              urlPattern: /^\/api\//,
              handler: 'NetworkOnly'
            }
          ]
        },
        navigateFallbackDenylist: [/^\/api\//]
      })
    ],
    // Proxy solo afecta al servidor de desarrollo (npm run dev)
    server: {
      proxy: {
        '/api': {
          target,
          changeOrigin: true
        }
      }
    }
  })
}