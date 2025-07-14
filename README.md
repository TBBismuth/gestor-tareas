# 📂 Gestor de Tareas

API REST desarrollada en Spring Boot para gestionar tareas multiusuario, con autenticación JWT, control de categorías y filtros.
Desarrollado unicamente como proyecto personal didáctico para avanzar y ampliar mis conocimientos

---

## ✨ Características actuales

* Registro y login de usuarios con contraseña encriptada (BCrypt) y generación de token JWT.
* Seguridad en todos los endpoints (requiere token salvo registro y login).
* CRUD completo de tareas:

  * Crear, editar, eliminar, listar.
  * Marcar como completadas, con almacenamiento de quién las completó.
  * Estado automático calculado (EN CURSO, COMPLETADA, VENCIDA, CON RETRASO).
* CRUD de categorías.
* Filtrado de tareas por prioridad, estado, categoría, tiempo estimado y palabras clave.
* Validaciones en entradas (campos requeridos, fechas futuras, tiempo >0, etc.).
* Documentación Swagger/OpenAPI.
* Configuración flexible mediante archivo de propiedades de ejemplo.
* Estructura clara con DTOs (`TareaRequest`, `TareaResponse`, `UsuarioRequest`, etc.).
* Excepciones centralizadas con manejador global.

---

## ⚙️ Requisitos

* Java 17 o superior
* Maven
* MariaDB (u otro SGBD compatible configurado)

---

## 🛠️ Configuración

Por seguridad, **no se incluye `application.properties`** en el repositorio.
En su lugar, encontrarás un archivo de ejemplo:

`src/main/resources/application-example.properties`

Cópialo renombrado como `application.properties` y edita los datos de conexión a la base de datos y la clave JWT:

```
spring.datasource.url=jdbc:mariadb://localhost:3306/gestor_tareas
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
gestorapp.jwt.secret=tu_clave_jwt_segura
```

---

## ▶️ Ejecución

Para compilar y arrancar la aplicación localmente:

```
mvn clean install
mvn spring-boot:run
```

Por defecto estará disponible en:

[http://localhost:8080](http://localhost:8080)

---

## 🔐 Autenticación

El proyecto utiliza JWT para proteger los endpoints.
Sigue estos pasos:

**1️⃣ Registrar un usuario**

POST `/api/usuario/add`

Body JSON:

```
{
  "nombre": "Usuario de ejemplo",
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}
```

**2️⃣ Login**

POST `/api/usuario/login`

Body JSON:

```
{
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}
```

La respuesta incluirá un campo `token` que debes usar en el header Authorization.

**3️⃣ Usar el token**

Añade la cabecera en tus peticiones:

```
Authorization: Bearer <token>
```

---

## 🛣️ Principales endpoints

### Usuarios

* POST /api/usuario/add – Registrar usuario
* POST /api/usuario/login – Login
* GET /api/usuario – Listar todos los usuarios
* GET /api/usuario/{id} – Obtener usuario por ID
* GET /api/usuario/email/{email} – Obtener usuario por email
* DELETE /api/usuario/delete/{id} – Eliminar usuario

### Tareas

* GET /api/tareas – Listar tareas del usuario autenticado
* GET /api/tareas/{id} – Obtener tarea por ID
* POST /api/tareas/add – Crear tarea
* PUT /api/tareas/update/{id} – Modificar tarea
* PATCH /api/tareas/completar/{id} – Marcar tarea como completada
* DELETE /api/tareas/delete/{id} – Eliminar tarea
* GET /api/tareas/filtrar/estado/{estado} – Filtrar por estado
* GET /api/tareas/filtrar/categoria/{categoria} – Filtrar por categoría
* GET /api/tareas/filtrar/prioridad/{prioridad} – Filtrar por prioridad
* GET /api/tareas/filtrar/tiempo/{tiempo} – Filtrar por tiempo estimado
* GET /api/tareas/filtrar/palabras/{palabrasClave} – Filtrar por palabras clave
* GET /api/tareas/titulo – Listar ordenadas por título
* GET /api/tareas/tiempo – Listar ordenadas por tiempo
* GET /api/tareas/prioridad – Listar ordenadas por prioridad
* GET /api/tareas/fecha – Listar ordenadas por fecha de entrega
* GET /api/tareas/hoy – Tareas con fecha de entrega hoy

### Categorías

* GET /api/categorias – Listar todas
* GET /api/categorias/nombre/{nombreParcial} – Buscar por nombre
* POST /api/categorias/add – Crear categoría
* PUT /api/categorias/update/{id} – Modificar categoría
* DELETE /api/categorias/delete/{id} – Eliminar categoría

---

## 📖 Documentación Swagger

La documentación de la API está disponible mientras la aplicación está levantada:

[http://localhost:8080/documentacion-api](http://localhost:8080/documentacion-api)

Desde ahí puedes probar los endpoints, ver los schemas y descargar la definición OpenAPI.

---

## 🧪 Estado actual y pendientes

Actualmente, el backend incluye la mayoría de funcionalidades básicas.

**Pendientes de implementar próximamente:**

* Tests unitarios y de integración con JUnit
* Algoritmo de priorización inteligente de tareas
* Notificaciones y recordatorios
* Roles y permisos avanzados
* Frontend web
* Despliegue en un entorno productivo

---

## ✍️ Autor y licencia

Proyecto desarrollado por \[Miguel Guerrero Murillo].
Repositorio: https://github.com/TBBismuth/gestor-tareas.git
Licencia MIT.

---

> 🚀 **Nota:** Este proyecto está optimizado para Eclipse; incluye archivos `.classpath` y `.settings/` para facilitar la configuración inmediata al abrirlo en este IDE.

