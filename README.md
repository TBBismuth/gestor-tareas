# 📂 Gestor de Tareas

> Spanish version available below.

**Gestor de Tareas** is a full-stack task management application built as a final project for **Desarrollo de Aplicaciones Multiplataforma (DAM)**. It includes a **Spring Boot REST API**, a **React + Vite PWA frontend**, JWT authentication, collaborative groups, advanced filtering, smart task ordering, internal notifications and Web Push support.

The project started as an educational backend/API project and evolved into a deployed web application with a cloud backend, cloud database and installable PWA frontend.

---

## ✨ Main Features

### Authentication and account access

- User registration and login.
- Password encryption with BCrypt.
- JWT-based authentication using access tokens and refresh tokens.
- Protected endpoints using `Authorization: Bearer <token>`.
- Authenticated user endpoint with `GET /api/usuario/me`.
- Authenticated account deletion with `DELETE /api/usuario/me`.

### Task management

- Create, read, update and delete personal tasks.
- Mark tasks as completed.
- Automatically calculated task status:
  - `EN_CURSO`
  - `VENCIDA`
  - `COMPLETADA`
  - `COMPLETADA_CON_RETRASO`
  - `SIN_FECHA`
- Due date, priority, estimated time, description and category support.
- Views for tasks due today, upcoming tasks and overdue tasks.

### Categories

- User-owned categories.
- Protected default categories created when a user registers.
- Category color and icon support.
- Category create, update, search and delete operations.
- Safe category deletion: tasks remain in the system and become uncategorized.

### Groups and collaborative assignments

- Group creation and management.
- Invitation code flow.
- Group roles: creator, admin and member.
- Member management, role changes, ownership transfer and group leave/delete flows.
- Group task assignments to all members or selected members.
- Individual task generation per assignee.
- Assignment tracking, delivery validation and reopening with review comments.

### Advanced filtering and smart ordering

- Combined filtering over personal and group-origin tasks.
- Filter by origin, group, category, multiple priorities, multiple states, keywords, maximum estimated time, exact due date, due date until and ordering criterion.
- Saved filter state per authenticated user.
- Smart ordering criterion based on a deterministic scoring heuristic.
- Dedicated recommended tasks endpoint.

### Notifications and reminders

- Internal notification tray.
- Active notification counter.
- Close one notification or close all notifications.
- Notification preferences per user.
- 24-hour-before-due-date alerts.
- Smart reminder per task.
- Group assignment notifications.
- Web Push subscription registration and deletion.
- Browser/system notifications when supported and allowed by the user.

### Frontend / PWA

- React + Vite frontend.
- Installable PWA behavior.
- Light/dark theme support.
- Responsive layout for desktop and mobile.
- Mobile-friendly task cards, filters, notification modal, auth screens and group assignment modal.
- Optimized mobile card backgrounds using WebP assets.

---

## 🧱 Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- JWT
- Swagger/OpenAPI
- Web Push with VAPID
- Maven

### Frontend

- React
- Vite
- JavaScript
- TanStack Query
- Axios
- React Router
- React Hook Form
- Tailwind CSS / centralized CSS tokens
- Service Worker / PWA

### Deployment

- Backend: Render
- Frontend: Vercel
- Database: Neon PostgreSQL
- Version control: Git + GitHub

---

## ⚙️ Requirements

### Backend

- Java 21 recommended.
- Maven Wrapper included.
- PostgreSQL database.

### Frontend

- Node.js 20+ recommended.
- npm.

---

## 🛠️ Local Configuration

### Backend configuration

The real local `application.properties` file is not versioned if it contains credentials or secrets. Use the example configuration file as a template and create your own local file.

Typical backend values include:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestor_tareas
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
jwt.secret=your_jwt_secret

app.webpush.enabled=false
app.webpush.vapid-public-key=
app.webpush.vapid-private-key=
app.webpush.vapid-subject=mailto:admin@example.com
app.webpush.default-url=/app
```

In production, sensitive values are configured as environment variables in Render.

### Frontend configuration

The frontend can use a local `.env` file based on `.env.example`.

Example:

```env
VITE_WEBPUSH_PUBLIC_KEY=your_public_vapid_key
```

The VAPID public key can exist in the frontend. The private VAPID key must only exist in the backend.

---

## ▶️ Running Locally

### Backend

From the backend folder:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend usually runs at:

```text
http://localhost:8080
```

### Frontend

From the `frontend-v2` folder:

```bash
npm install
npm run dev
```

The Vite development server usually runs at:

```text
http://localhost:5173
```

### Production build

Backend:

```bash
./mvnw -DskipTests clean package
```

Frontend:

```bash
npm run build
```

---

## 🔐 Authentication Workflow

1. Register a new user:

```http
POST /api/usuario/add
```

Example body:

```json
{
  "nombre": "Example User",
  "email": "user@example.com",
  "password": "your_password"
}
```

2. Login:

```http
POST /api/usuario/login
```

Example body:

```json
{
  "email": "user@example.com",
  "password": "your_password"
}
```

3. Use the access token in protected requests:

```http
Authorization: Bearer <access_token>
```

4. Refresh the session when the access token expires:

```http
POST /api/usuario/refresh
```

The refresh token is only used to obtain a new access token. It should not be used as a normal API access token.

---

## 🛣️ Main API Areas

All routes are prefixed with `/api`.

### Users

- `POST /api/usuario/add`
- `POST /api/usuario/login`
- `POST /api/usuario/refresh`
- `GET /api/usuario/me`
- `DELETE /api/usuario/me`

### Tasks

- `GET /api/tarea`
- `GET /api/tarea/{id}`
- `POST /api/tarea/add`
- `PUT /api/tarea/update/{id}`
- `PATCH /api/tarea/completar/{id}`
- `DELETE /api/tarea/delete/{id}`
- `GET /api/tarea/hoy`
- `GET /api/tarea/proximas`
- `GET /api/tarea/vencidas`
- `POST /api/tarea/filtrar-combinado`
- `POST /api/tarea/recomendadas`
- `GET /api/tarea/filtro-combinado/save`
- `PUT /api/tarea/filtro-combinado/save`
- `PATCH /api/tarea/{id}/recordatorio-inteligente`
- `GET /api/tarea/asignadas-grupo`
- `GET /api/tarea/asignadas-grupo/{idGrupo}`

### Categories

- `GET /api/categoria`
- `GET /api/categoria/nombre/{nombreParcial}`
- `POST /api/categoria/add`
- `PUT /api/categoria/update/{id}`
- `DELETE /api/categoria/delete/{id}`

### Groups

- `GET /api/grupo`
- `GET /api/grupo/{id}`
- `POST /api/grupo/add`
- `PUT /api/grupo/update/{id}`
- `PATCH /api/grupo/active/{id}`
- `DELETE /api/grupo/delete/{id}`
- `POST /api/grupo/join`
- `GET /api/grupo/{id}/miembros`
- `POST /api/grupo/{id}/miembros/add`
- `DELETE /api/grupo/{id}/miembros/delete/{idUsuario}`
- `PATCH /api/grupo/{id}/miembros/rol/{idUsuario}`
- `PATCH /api/grupo/{id}/transfer-ownership`
- `DELETE /api/grupo/{id}/leave`
- `GET /api/grupo/{id}/invitation-code`
- `POST /api/grupo/{id}/invitation-code/regenerate`

### Group assignments

- `POST /api/grupo/{id}/asignaciones/add`
- `GET /api/grupo/{id}/asignaciones`
- `GET /api/grupo/{id}/asignaciones/{idAsignacion}`
- `PATCH /api/grupo/asignaciones/{idAsignacionGrupoMiembro}/validate`
- `PATCH /api/grupo/asignaciones/{idAsignacionGrupoMiembro}/reopen`

### Notifications

- `GET /api/notificaciones`
- `GET /api/notificaciones/count`
- `PATCH /api/notificaciones/{id}/cerrar`
- `PATCH /api/notificaciones/cerrar-todas`
- `GET /api/notificaciones/preferencias`
- `PUT /api/notificaciones/preferencias`
- `POST /api/notificaciones/push-subscripciones`
- `DELETE /api/notificaciones/push-subscripciones`

### Health check

- `GET /api/health`

---

## 📖 Swagger API Documentation

Swagger UI is available in the deployed backend:

```text
https://gestor-backend-tnsg.onrender.com/swagger-ui/index.html#/
```

When running locally, Swagger is available from the local backend URL if enabled by configuration.

---

## ✅ Testing and Validation

The project has an automatic test base created during earlier development stages, including unit, repository, controller and flow tests.

However, part of that test suite is currently outdated because the backend evolved significantly: refresh token authentication, groups, assignments, advanced filtering, smart ordering, notifications and Web Push changed several contracts.

Current validation has mainly been performed through:

- Backend compilation with Maven.
- Backend packaging when needed.
- API testing with Postman.
- Swagger checks.
- Frontend production builds with `npm run build`.
- Manual frontend testing.
- PWA installation checks.
- Production validation on Vercel + Render + Neon.

Useful commands:

```bash
./mvnw -DskipTests compile
./mvnw -DskipTests clean package
npm run build
```

Updating and cleaning the automatic test suite is planned as future technical work.

---

## 🚀 Current Status

The current version includes:

- Backend API deployed on Render.
- Frontend PWA deployed on Vercel.
- PostgreSQL database hosted on Neon.
- JWT access/refresh authentication.
- Personal tasks and categories.
- Collaborative groups and assignments.
- Advanced combined filtering.
- Smart task ordering.
- Internal notifications.
- Web Push support.
- Responsive mobile interface.

---

## 🧪 Future Plans

Planned improvements include:

- Email verification.
- Captcha during registration.
- Profile and account settings.
- Change email/password flows.
- More advanced notification preferences.
- Google login/register.
- Google Calendar integration.
- Calendar view.
- Personal and group statistics.
- CSV/PDF import and export.
- More complete group review history.
- Real shared group tasks without individual duplication.
- Native mobile app with React Native.
- Accessibility improvements.
- Monitoring and advanced logs.
- Database backups and recovery strategy.
- Versioned migrations with Flyway or Liquibase.
- Updated automatic test suite.

---

## ✍️ Author and License

Developed by **Miguel Guerrero Murillo**.

GitHub: https://github.com/TBBismuth

Licensed under the **MIT License**.

---

# 📂 Gestor de Tareas (Versión en Español)

> English version available above.

**Gestor de Tareas** es una aplicación full stack de gestión de tareas desarrollada como proyecto final de **Desarrollo de Aplicaciones Multiplataforma (DAM)**. Incluye una **API REST con Spring Boot**, un **frontend PWA con React + Vite**, autenticación JWT, grupos colaborativos, filtros avanzados, ordenación inteligente de tareas, notificaciones internas y soporte de Web Push.

El proyecto comenzó como una aplicación educativa centrada en backend y API, y evolucionó hasta convertirse en una aplicación web desplegada, con backend en la nube, base de datos en la nube y frontend instalable como PWA.

---

## ✨ Características principales

### Autenticación y cuenta

- Registro e inicio de sesión de usuarios.
- Contraseñas cifradas con BCrypt.
- Autenticación mediante JWT con access token y refresh token.
- Endpoints protegidos mediante `Authorization: Bearer <token>`.
- Consulta de la cuenta autenticada con `GET /api/usuario/me`.
- Eliminación de la cuenta autenticada con `DELETE /api/usuario/me`.

### Gestión de tareas

- Crear, consultar, actualizar y eliminar tareas personales.
- Marcar tareas como completadas.
- Estado de tarea calculado automáticamente:
  - `EN_CURSO`
  - `VENCIDA`
  - `COMPLETADA`
  - `COMPLETADA_CON_RETRASO`
  - `SIN_FECHA`
- Soporte para fecha de entrega, prioridad, tiempo estimado, descripción y categoría.
- Vistas de tareas de hoy, próximas a vencer y vencidas.

### Categorías

- Categorías propias por usuario.
- Categorías base protegidas creadas al registrar una cuenta.
- Soporte de color e icono en categorías.
- Crear, editar, buscar y eliminar categorías.
- Eliminación segura de categorías: las tareas se conservan y quedan sin categoría.

### Grupos y asignaciones colaborativas

- Creación y gestión de grupos.
- Unión a grupos mediante código de invitación.
- Roles de grupo: creador, admin y miembro.
- Gestión de miembros, cambios de rol, transferencia de ownership y salida/eliminación de grupos.
- Asignación de tareas a todo el grupo o a miembros seleccionados.
- Generación de tareas individuales por destinatario.
- Seguimiento de asignaciones, validación de entregas y reapertura con comentarios de revisión.

### Filtro avanzado y ordenación inteligente

- Filtro combinado sobre tareas personales y tareas con origen grupal.
- Filtro por origen, grupo, categoría, múltiples prioridades, múltiples estados, palabras clave, tiempo máximo, fecha exacta, fecha hasta y criterio de ordenación.
- Estado del filtro guardado por usuario.
- Criterio de ordenación inteligente basado en una heurística determinista.
- Endpoint dedicado de tareas recomendadas.

### Notificaciones y recordatorios

- Bandeja interna de notificaciones.
- Contador de notificaciones activas.
- Cierre individual o global de notificaciones.
- Preferencias de notificación por usuario.
- Avisos 24 horas antes del vencimiento.
- Recordatorio inteligente por tarea.
- Notificaciones por asignaciones de grupo.
- Registro y desactivación de suscripciones Web Push.
- Notificaciones del navegador/sistema cuando el navegador lo soporta y el usuario concede permisos.

### Frontend / PWA

- Frontend desarrollado con React + Vite.
- Comportamiento instalable como PWA.
- Soporte de tema claro/oscuro.
- Diseño responsive para escritorio y móvil.
- Tarjetas, filtros, modal de notificaciones, pantallas de autenticación y modal de asignaciones adaptados a móvil.
- Fondos móviles de tarjetas optimizados mediante WebP.

---

## 🧱 Stack tecnológico

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- JWT
- Swagger/OpenAPI
- Web Push con VAPID
- Maven

### Frontend

- React
- Vite
- JavaScript
- TanStack Query
- Axios
- React Router
- React Hook Form
- Tailwind CSS / tokens CSS centralizados
- Service Worker / PWA

### Despliegue

- Backend: Render
- Frontend: Vercel
- Base de datos: Neon PostgreSQL
- Control de versiones: Git + GitHub

---

## ⚙️ Requisitos

### Backend

- Java 21 recomendado.
- Maven Wrapper incluido.
- Base de datos PostgreSQL.

### Frontend

- Node.js 20+ recomendado.
- npm.

---

## 🛠️ Configuración local

### Configuración del backend

El archivo local real `application.properties` no se versiona si contiene credenciales o secretos. Debe usarse el archivo de ejemplo como plantilla y crear una configuración local propia.

Valores habituales del backend:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestor_tareas
spring.datasource.username=tu_usuario_db
spring.datasource.password=tu_password_db
jwt.secret=tu_secreto_jwt

app.webpush.enabled=false
app.webpush.vapid-public-key=
app.webpush.vapid-private-key=
app.webpush.vapid-subject=mailto:admin@example.com
app.webpush.default-url=/app
```

En producción, los valores sensibles se configuran como variables de entorno en Render.

### Configuración del frontend

El frontend puede utilizar un archivo `.env` local basado en `.env.example`.

Ejemplo:

```env
VITE_WEBPUSH_PUBLIC_KEY=tu_clave_publica_vapid
```

La clave pública VAPID puede existir en el frontend. La clave privada VAPID debe existir únicamente en el backend.

---

## ▶️ Ejecución local

### Backend

Desde la carpeta del backend:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

El backend normalmente se levanta en:

```text
http://localhost:8080
```

### Frontend

Desde la carpeta `frontend-v2`:

```bash
npm install
npm run dev
```

El servidor de desarrollo de Vite normalmente se levanta en:

```text
http://localhost:5173
```

### Build de producción

Backend:

```bash
./mvnw -DskipTests clean package
```

Frontend:

```bash
npm run build
```

---

## 🔐 Flujo de autenticación

1. Registrar un nuevo usuario:

```http
POST /api/usuario/add
```

Ejemplo de cuerpo:

```json
{
  "nombre": "Usuario Ejemplo",
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}
```

2. Iniciar sesión:

```http
POST /api/usuario/login
```

Ejemplo de cuerpo:

```json
{
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}
```

3. Usar el access token en peticiones protegidas:

```http
Authorization: Bearer <access_token>
```

4. Renovar sesión cuando caduca el access token:

```http
POST /api/usuario/refresh
```

El refresh token solo se utiliza para obtener un nuevo access token. No debe utilizarse como token normal de acceso a la API.

---

## 🛣️ Áreas principales de la API

Todas las rutas comienzan por `/api`.

### Usuarios

- `POST /api/usuario/add`
- `POST /api/usuario/login`
- `POST /api/usuario/refresh`
- `GET /api/usuario/me`
- `DELETE /api/usuario/me`

### Tareas

- `GET /api/tarea`
- `GET /api/tarea/{id}`
- `POST /api/tarea/add`
- `PUT /api/tarea/update/{id}`
- `PATCH /api/tarea/completar/{id}`
- `DELETE /api/tarea/delete/{id}`
- `GET /api/tarea/hoy`
- `GET /api/tarea/proximas`
- `GET /api/tarea/vencidas`
- `POST /api/tarea/filtrar-combinado`
- `POST /api/tarea/recomendadas`
- `GET /api/tarea/filtro-combinado/save`
- `PUT /api/tarea/filtro-combinado/save`
- `PATCH /api/tarea/{id}/recordatorio-inteligente`
- `GET /api/tarea/asignadas-grupo`
- `GET /api/tarea/asignadas-grupo/{idGrupo}`

### Categorías

- `GET /api/categoria`
- `GET /api/categoria/nombre/{nombreParcial}`
- `POST /api/categoria/add`
- `PUT /api/categoria/update/{id}`
- `DELETE /api/categoria/delete/{id}`

### Grupos

- `GET /api/grupo`
- `GET /api/grupo/{id}`
- `POST /api/grupo/add`
- `PUT /api/grupo/update/{id}`
- `PATCH /api/grupo/active/{id}`
- `DELETE /api/grupo/delete/{id}`
- `POST /api/grupo/join`
- `GET /api/grupo/{id}/miembros`
- `POST /api/grupo/{id}/miembros/add`
- `DELETE /api/grupo/{id}/miembros/delete/{idUsuario}`
- `PATCH /api/grupo/{id}/miembros/rol/{idUsuario}`
- `PATCH /api/grupo/{id}/transfer-ownership`
- `DELETE /api/grupo/{id}/leave`
- `GET /api/grupo/{id}/invitation-code`
- `POST /api/grupo/{id}/invitation-code/regenerate`

### Asignaciones de grupo

- `POST /api/grupo/{id}/asignaciones/add`
- `GET /api/grupo/{id}/asignaciones`
- `GET /api/grupo/{id}/asignaciones/{idAsignacion}`
- `PATCH /api/grupo/asignaciones/{idAsignacionGrupoMiembro}/validate`
- `PATCH /api/grupo/asignaciones/{idAsignacionGrupoMiembro}/reopen`

### Notificaciones

- `GET /api/notificaciones`
- `GET /api/notificaciones/count`
- `PATCH /api/notificaciones/{id}/cerrar`
- `PATCH /api/notificaciones/cerrar-todas`
- `GET /api/notificaciones/preferencias`
- `PUT /api/notificaciones/preferencias`
- `POST /api/notificaciones/push-subscripciones`
- `DELETE /api/notificaciones/push-subscripciones`

### Health check

- `GET /api/health`

---

## 📖 Documentación Swagger

Swagger UI está disponible en el backend desplegado:

```text
https://gestor-backend-tnsg.onrender.com/swagger-ui/index.html#/
```

En local, Swagger estará disponible desde la URL local del backend si está habilitado por configuración.

---

## ✅ Pruebas y validación

El proyecto cuenta con una base de tests automáticos creada durante fases anteriores del desarrollo, incluyendo pruebas unitarias, de repositorio, de controladores y de flujo.

Sin embargo, parte de esa batería está actualmente desactualizada debido a que el backend evolucionó de forma importante: autenticación con refresh token, grupos, asignaciones, filtro avanzado, ordenación inteligente, notificaciones y Web Push cambiaron varios contratos.

La validación actual se ha realizado principalmente mediante:

- Compilación del backend con Maven.
- Empaquetado del backend cuando ha sido necesario.
- Pruebas de API con Postman.
- Comprobaciones con Swagger.
- Builds del frontend con `npm run build`.
- Pruebas manuales del frontend.
- Comprobación de instalación PWA.
- Validación en producción sobre Vercel + Render + Neon.

Comandos útiles:

```bash
./mvnw -DskipTests compile
./mvnw -DskipTests clean package
npm run build
```

Actualizar y sanear la batería automática de tests queda como trabajo técnico futuro.

---

## 🚀 Estado actual

La versión actual incluye:

- Backend API desplegado en Render.
- Frontend PWA desplegado en Vercel.
- Base de datos PostgreSQL alojada en Neon.
- Autenticación JWT con access token y refresh token.
- Tareas personales y categorías.
- Grupos y asignaciones colaborativas.
- Filtro combinado avanzado.
- Ordenación inteligente de tareas.
- Notificaciones internas.
- Soporte Web Push.
- Interfaz responsive para móvil.

---

## 🧪 Futuras mejoras

Mejoras previstas:

- Validación de email.
- Captcha en registro.
- Perfil de usuario y ajustes de cuenta.
- Cambio de email/contraseña.
- Preferencias de notificación más avanzadas.
- Login/registro con Google.
- Integración con Google Calendar.
- Vista calendario.
- Estadísticas personales y por grupo.
- Importación y exportación CSV/PDF.
- Histórico más completo de revisiones de grupo.
- Tareas grupales compartidas reales sin duplicación individual.
- Aplicación móvil nativa con React Native.
- Mejoras de accesibilidad.
- Monitorización y logs avanzados.
- Copias de seguridad y estrategia de recuperación.
- Migraciones versionadas con Flyway o Liquibase.
- Actualización de la suite automática de tests.

---

## ✍️ Autor y licencia

Desarrollado por **Miguel Guerrero Murillo**.

GitHub: https://github.com/TBBismuth

Licencia: **MIT**.
