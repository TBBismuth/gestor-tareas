📂 Gestor de Tareas

Note: the Spanish version of this README is provided further below.

RESTful API built with Spring Boot for multi-user task management, featuring JWT-based authentication, category management, and flexible task filtering. Developed as a personal educational project to deepen and broaden skills in Spring Boot and API development.



✨ Features

User Authentication: User registration and login with encrypted passwords (BCrypt) and JWT token generation. All endpoints are secured with JWT (except registration and login).

Task Management (CRUD): Full create, read, update, delete functionality for tasks. Tasks can be marked as completed, and the user who completed the task is recorded. Each task has an automatically calculated status (e.g. EN_CURSO, COMPLETADA, VENCIDA, COMPLETADA_CON_RETRASO, SIN_FECHA – in progress, completed, expired, completed late, or no deadline).

Category Management: Create, edit, delete, and list categories for organizing tasks.

Task Filtering & Sorting: Filter tasks by priority, status, category, estimated time, or keywords in title/description. Also supports sorting task lists by title, priority, estimated time, or due date, and a quick view of tasks due today.

Input Validation: Robust validation for request data (required fields, future dates for due dates, positive time estimates, etc.), ensuring reliability of inputs.

API Documentation: Interactive API documentation is available via Swagger/OpenAPI. When the application is running, Swagger UI can be accessed at http://localhost:8080/documentacion-api to explore and test the endpoints, view schemas, and download the OpenAPI specification.

Configurable Settings: Flexible configuration using an external properties file. An example config file (application-example.properties) is provided to set up database connection details and the JWT secret key.

DTO Architecture: Clear structure separating domain models and transfer objects (e.g. TareaRequest, TareaResponse, UsuarioRequest, UsuarioResponse) for cleaner API responses and requests.

Global Exception Handling: Centralized exception management using a global exception handler to produce consistent error responses.

Comprehensive Testing: Extensive unit and integration tests are included (JUnit 5 with Spring Boot testing). The test suite covers controllers, services, repositories, and exception handlers to ensure code reliability. (Tests run with an in-memory H2 database for isolation.)



⚙️ Requirements

Java 17 or higher: Developed and tested on Java 21, but Java 17+ is required at minimum.

Maven: Used for build and dependency management (Maven Wrapper or Maven 3.8+ recommended).

Database: MariaDB (or another compatible SQL database). The default configuration assumes MariaDB on localhost.



🛠️ Configuration

For security, the repository does not include a production application.properties. Instead, an example file is provided:

src/main/resources/application-example.properties

Copy this file and rename it to application.properties in the same directory. Then update the configuration values for your environment, especially the database connection and JWT secret key. For example:

spring.datasource.url=jdbc:mariadb://localhost:3306/gestor_tareas
spring.datasource.username=<your_db_username>
spring.datasource.password=<your_db_password>
jwt.secret=<your_jwt_secret_key>

Ensure your database is running and the credentials match your setup.



▶️ Running the Application

To build and run the application locally:

mvn clean install
mvn spring-boot:run

By default, the server will start at . You can then use the API (see endpoints below) or access the Swagger UI as described above.



🔐 Authentication Workflow

This API uses JWT (JSON Web Tokens) for securing endpoints. All protected endpoints require an Authorization: Bearer <token> header with a valid token. Typical usage flow:

Register a new user:
POST /api/usuario/add
Body (JSON):


{
  "nombre": "Usuario Ejemplo",
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}

This creates a new user account. (No token required for this endpoint.)

Login with the user credentials:
POST /api/usuario/login
Body (JSON):


{
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}

On success, the response will include a JWT in a field (for example, "token": "<JWT>"). This endpoint also does not require a token.

Authorize subsequent requests:
For any further API requests, include the JWT in the Authorization header:


Authorization: Bearer <token>

You can now access protected endpoints with the token. The token is valid for 24 hours by default.



🛣️ Main API Endpoints

Below is a summary of the primary API endpoints. All routes are prefixed with /api.

Users

POST /api/usuario/add – Register a new user

POST /api/usuario/login – Authenticate and obtain a JWT

GET /api/usuario – List all users

GET /api/usuario/{id} – Get user details by ID

GET /api/usuario/email/{email} – Get user details by email

DELETE /api/usuario/delete/{id} – Delete a user by ID

Tasks

GET /api/tarea – List all tasks of the authenticated user

GET /api/tarea/{id} – Get a specific task by ID (if owned by the authenticated user)

POST /api/tarea/add – Create a new task

PUT /api/tarea/update/{id} – Update an existing task

PATCH /api/tarea/completar/{id} – Mark a task as completed

DELETE /api/tarea/delete/{id} – Delete a task

GET /api/tarea/estado/{id} – Get the status of a specific task (by ID)

GET /api/tarea/filtrar/estado/{estado} – Filter tasks by status

GET /api/tarea/filtrar/categoria/{idCategoria} – Filter tasks by category (category ID)

GET /api/tarea/filtrar/prioridad/{prioridad} – Filter tasks by priority

GET /api/tarea/filtrar/tiempo/{minutos} – Filter tasks by estimated time (in minutes)

GET /api/tarea/filtrar/palabras/{keywords} – Filter tasks by keywords contained in title or description

GET /api/tarea/titulo – List tasks sorted alphabetically by title

GET /api/tarea/tiempo – List tasks sorted by estimated time

GET /api/tarea/prioridad – List tasks sorted by priority

GET /api/tarea/fecha – List tasks sorted by due date

GET /api/tarea/hoy – List tasks with a due date of today

Categories

GET /api/categoria – List all categories

GET /api/categoria/nombre/{nombre} – Search categories by name (partial match)

POST /api/categoria/add – Create a new category

PUT /api/categoria/update/{id} – Update an existing category

DELETE /api/categoria/delete/{id} – Delete a category

(All endpoints above (except user registration and login) require a valid JWT in the Authorization header.)



📖 Swagger API Documentation

As mentioned, this project integrates Swagger UI (via Springdoc OpenAPI) for API documentation. Once the app is running, navigate to  to view the interactive documentation. From there you can try out API calls directly in the browser, see detailed models (schemas), and even download the OpenAPI (Swagger) specification for integration or analysis.



✅ Testing

This project includes a comprehensive test suite. To run all tests, execute:

mvn test

The tests are written with JUnit 5 and use Spring Boot's testing support. They cover all layers of the application (controllers, services, repositories, and exception handling). An in-memory H2 database is automatically used for tests, so you do not need a running MariaDB instance to execute the test suite. Test data is loaded and torn down for each test, ensuring isolation.



🧪 Current Status and Future Plans

At present, the backend implementation is complete, and the application’s core features (as listed above) are fully functional and tested.

Planned features for future versions:

Intelligent task prioritization algorithm – e.g. automatically rank or highlight tasks based on urgency, deadlines, or other criteria.

Notifications & reminders – system to notify users of upcoming deadlines or send reminders for pending tasks.

Advanced user roles & permissions – implement role-based access control (e.g. admin users, read-only roles) for more nuanced security.

Web frontend – a user-friendly web interface for the API (to be developed, since currently only the REST API backend exists).

Deployment configuration – containerization or cloud deployment (Docker, CI/CD, etc.) for running the application in a production environment.

(These features are not yet implemented at the time of writing and are intended for future releases.)



✍️ Author and License

Developed by Miguel Guerrero Murillo.
Repository: 
Licensed under the MIT License.



🚀 Note: This project is optimized for use with Eclipse IDE (the repository includes Eclipse configuration files like .classpath and the .settings/ folder to simplify setup). You can import the project directly into Eclipse without additional configuration.



📂 Gestor de Tareas (Versión en Español)

Nota: Este README está disponible en inglés arriba. A continuación, se presenta la versión en español.

API REST desarrollada con Spring Boot para gestionar tareas multiusuario, con autenticación JWT, gestión de categorías y filtrado de tareas. Desarrollado como un proyecto personal didáctico para avanzar y ampliar mis conocimientos en desarrollo de APIs con Spring Boot.



✨ Características

Autenticación de Usuarios: Registro y login de usuarios con contraseña encriptada (BCrypt) y generación de token JWT. Todos los endpoints están protegidos con JWT (salvo los de registro y login).

Gestión de Tareas (CRUD): Completo create, read, update, delete para tareas. Es posible marcar las tareas como completadas, almacenando quién las completó. Cada tarea tiene un estado calculado automáticamente (por ejemplo: EN_CURSO, COMPLETADA, VENCIDA, COMPLETADA_CON_RETRASO, SIN_FECHA).

Gestión de Categorías: Crear, editar, eliminar y listar categorías para organizar las tareas.

Filtrado y Ordenación de Tareas: Filtrar las tareas por prioridad, estado, categoría, tiempo estimado o palabras clave (en el título o la descripción). También se puede obtener listados ordenados por título, prioridad, tiempo estimado o fecha de entrega, así como consultar rápidamente las tareas con fecha de hoy.

Validación de Datos: Validaciones robustas en las entradas (campos requeridos, fechas de entrega futuras, tiempo estimado positivo, etc.) para garantizar la integridad de los datos.

Documentación de la API: Documentación interactiva disponible mediante Swagger/OpenAPI. Con la aplicación en funcionamiento, se puede acceder a la interfaz Swagger UI en http://localhost:8080/documentacion-api para explorar y probar los endpoints, ver los esquemas de datos y descargar la especificación OpenAPI.

Configuración Flexible: Uso de un archivo de propiedades externo para la configuración. Se incluye un archivo de ejemplo (application-example.properties) para facilitar la configuración de la conexión a base de datos y la clave secreta JWT.

Arquitectura DTO: Estructura clara que separa las entidades de la lógica de negocio de los objetos de transferencia de datos (DTO), por ejemplo TareaRequest, TareaResponse, UsuarioRequest, UsuarioResponse.

Manejador Global de Excepciones: Gestión centralizada de errores mediante un controlador global, lo que unifica el formato de las respuestas de error de la API.

Pruebas Automatizadas: Amplia cobertura de pruebas unitarias e integrales con JUnit 5 y Spring Boot. El conjunto de pruebas cubre controladores, servicios, repositorios y manejo de excepciones, asegurando la confiabilidad del código. (Las pruebas usan una base de datos H2 en memoria para aislar los datos.)



⚙️ Requisitos

Java 17 o superior: Desarrollado y probado con Java 21, se requiere al menos Java 17.

Maven: Utilizado para la compilación y gestión de dependencias (se recomienda Maven 3.8+ o el Maven Wrapper incluido).

Base de Datos: MariaDB (u otro SGBD SQL compatible). La configuración por defecto asume una base MariaDB local.



🛠️ Configuración

Por motivos de seguridad, no se incluye el archivo de propiedades de aplicación en el repositorio (application.properties). En su lugar, encontrarás un archivo de ejemplo:

src/main/resources/application-example.properties

Cópialo en el mismo directorio con el nombre application.properties y edita sus valores según tu entorno, especialmente los datos de conexión a la base de datos y la clave secreta JWT. Por ejemplo:

spring.datasource.url=jdbc:mariadb://localhost:3306/gestor_tareas
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
jwt.secret=tu_clave_secreta_jwt

Asegúrate de que tu base de datos esté en funcionamiento y de que las credenciales correspondan a tu configuración.



▶️ Ejecución de la Aplicación

Para compilar y ejecutar la aplicación localmente:

mvn clean install
mvn spring-boot:run

Por defecto, el servidor estará disponible en . Desde allí puedes utilizar la API (ver los endpoints listados abajo) o acceder a la documentación Swagger mencionada anteriormente.



🔐 Flujo de Autenticación

El proyecto utiliza JWT (JSON Web Tokens) para proteger los endpoints. Todos los endpoints (salvo registro y login) requieren en las peticiones la cabecera Authorization con un token válido. El flujo típico para usar la API es:

Registrar un nuevo usuario:
POST /api/usuario/add
Cuerpo (JSON):


{
  "nombre": "Usuario Ejemplo",
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}

Esto creará una nueva cuenta de usuario. (Este endpoint no requiere token.)

Iniciar sesión (login):
POST /api/usuario/login
Cuerpo (JSON):


{
  "email": "usuario@ejemplo.com",
  "password": "tu_password"
}

Si las credenciales son correctas, la respuesta incluirá un JWT en un campo (por ejemplo, "token": "<JWT>"). (Este endpoint tampoco requiere token.)

Usar el token para acceder a la API:
Para llamadas posteriores, incluye el JWT en la cabecera Authorization de tus peticiones:


Authorization: Bearer <token>

A partir de ese momento, podrás acceder a los endpoints protegidos con el token proporcionado. El token es válido por 24 horas de forma predeterminada.



🛣️ Endpoints Principales

A continuación se resumen los principales endpoints de la API. Todas las rutas comienzan con /api.

Usuarios

POST /api/usuario/add – Registrar un nuevo usuario

POST /api/usuario/login – Autenticar usuario y obtener JWT

GET /api/usuario – Listar todos los usuarios

GET /api/usuario/{id} – Obtener detalles de un usuario por ID

GET /api/usuario/email/{email} – Obtener detalles de un usuario por email

DELETE /api/usuario/delete/{id} – Eliminar un usuario por ID

Tareas

GET /api/tarea – Listar todas las tareas del usuario autenticado

GET /api/tarea/{id} – Obtener una tarea específica por ID (si pertenece al usuario autenticado)

POST /api/tarea/add – Crear una nueva tarea

PUT /api/tarea/update/{id} – Modificar una tarea existente

PATCH /api/tarea/completar/{id} – Marcar una tarea como completada

DELETE /api/tarea/delete/{id} – Eliminar una tarea

GET /api/tarea/estado/{id} – Consultar el estado de una tarea por ID

GET /api/tarea/filtrar/estado/{estado} – Filtrar tareas por estado

GET /api/tarea/filtrar/categoria/{idCategoria} – Filtrar tareas por categoría (ID de categoría)

GET /api/tarea/filtrar/prioridad/{prioridad} – Filtrar tareas por prioridad

GET /api/tarea/filtrar/tiempo/{minutos} – Filtrar tareas por tiempo estimado (en minutos)

GET /api/tarea/filtrar/palabras/{keywords} – Filtrar tareas por palabras clave contenidas en el título o la descripción

GET /api/tarea/titulo – Listar tareas ordenadas alfabéticamente por título

GET /api/tarea/tiempo – Listar tareas ordenadas por tiempo estimado

GET /api/tarea/prioridad – Listar tareas ordenadas por prioridad

GET /api/tarea/fecha – Listar tareas ordenadas por fecha de entrega

GET /api/tarea/hoy – Listar tareas cuya fecha de entrega es hoy

Categorías

GET /api/categoria – Listar todas las categorías

GET /api/categoria/nombre/{nombre} – Buscar categorías por nombre (coincidencia parcial)

POST /api/categoria/add – Crear una nueva categoría

PUT /api/categoria/update/{id} – Modificar una categoría existente

DELETE /api/categoria/delete/{id} – Eliminar una categoría

(Todos los endpoints anteriores, excepto registro de usuario y login, requieren un token JWT válido en la cabecera Authorization.)



📖 Documentación Swagger

Como se mencionó, el proyecto integra Swagger UI (Springdoc OpenAPI) para documentar la API. Una vez que la aplicación esté en ejecución, navega a  para ver la documentación interactiva. Desde allí podrás probar los endpoints directamente, revisar los modelos de datos (schemas) y descargar la especificación OpenAPI (Swagger) para su uso en otras herramientas.



✅ Pruebas

Este proyecto incluye una completa batería de pruebas automatizadas. Para ejecutar todos los tests, utiliza:

mvn test

Las pruebas están escritas con JUnit 5 y utilizan el soporte de Spring Boot para testing. Cubren todas las capas de la aplicación (controladores, servicios, repositorios y manejo de excepciones). Para aislar los tests, se utiliza una base de datos H2 en memoria, por lo que no se requiere tener MariaDB levantado para ejecutar la suite de pruebas. Los datos de prueba se cargan y limpian en cada test, asegurando independencia entre casos.



🧪 Estado Actual y Pendientes (Futuras Versiones)

Actualmente, el backend está completo y la aplicación incluye todas las funcionalidades básicas mencionadas anteriormente, con su correspondiente batería de pruebas automatizadas.

Funcionalidades previstas para futuras versiones:

Algoritmo inteligente de priorización de tareas – por ejemplo, ordenar o destacar automáticamente las tareas en función de su urgencia, fecha límite u otros criterios.

Notificaciones y recordatorios – sistema para notificar a los usuarios sobre fechas próximas de vencimiento o recordar tareas pendientes.

Roles de usuario y permisos avanzados – implementación de control de acceso por roles (p. ej., usuarios administradores, roles solo lectura) para una seguridad más granular.

Frontend web – una interfaz web para los usuarios (actualmente solo existe el backend API REST).

Despliegue en producción – configuración para desplegar la aplicación en un entorno productivo (contenedores Docker, CI/CD, nube, etc.).

(Estas características aún no están implementadas en el momento de redactar este documento y se consideran para futuras versiones del proyecto.)



✍️ Autor y Licencia

Desarrollado por Miguel Guerrero Murillo.
Repositorio: 
Licencia: MIT.



🚀 Nota: Este proyecto está optimizado para Eclipse IDE; el repositorio incluye archivos de configuración de Eclipse (como .classpath y la carpeta .settings/) para facilitar la configuración al importar el proyecto. Puedes importar el repositorio directamente en Eclipse sin configuraciones adicionales.

