# UniVibe Backend

## CS 2031 Desarrollo Basado en Plataforma

- Integrantes: [Complete aquí los nombres completos]

## Índice
- Portada
- Introducción
- Identificación del Problema o Necesidad
- Descripción de la Solución
- Tecnologías Utilizadas
- Modelo de Entidades
- Testing y Manejo de Errores
- Medidas de Seguridad Implementadas
- Eventos y Asincronía
- API REST y Controllers
- GitHub & Management
- Deployment
- Conclusión
- Apéndices

## Portada
**Título**: UniVibe Backend – Plataforma de Gestión de Eventos Universitarios

**Curso**: CS 2031 Desarrollo Basado en Plataforma

**Integrantes**: [Nombres completos]

## Introducción
Contexto y Objetivo. UniVibe surge para centralizar la oferta de eventos universitarios, simplificando el descubrimiento, registro y participación. El objetivo es brindar una API segura y modular para: registro/login, descubrimiento de eventos, inscripción con QR, gamificación, notificaciones en tiempo real, grupos y chat por evento, encuestas y sincronización con Google Calendar.

## Identificación del Problema o Necesidad
Los estudiantes suelen perder eventos por falta de difusión o fricción al registrarse. Organizar grupos y recopilar feedback también resulta difícil. Se requiere una solución con autenticación segura, roles, notificaciones y herramientas colaborativas.

### Objetivos del Proyecto
- Autenticación con JWT y control de roles.
- Gestión del ciclo de vida de eventos (crear, iniciar, finalizar).
- Registro gamificado con QR y check-in.
- Comunicación en tiempo real (notificaciones y chat) gated por estado del evento.
- Integración con Google Calendar y encuestas en vivo.

## Descripción de la Solución
### Funcionalidades Implementadas
- Registro y Login de Usuarios (JWT, BCrypt, validaciones)
- Descubrimiento Inteligente de Eventos (por categoría/estado)
- Inscripción Gamificada (QR único y check-in)
- Notificaciones Interactivas (WebSocket y email async)
- Integración con Google Calendar
- Grupos y Chats de Evento (chat activo solo en eventos LIVE)
- Encuestas y Feedback en Vivo

### Ampliaciones Clave
- Nuevo rol SERVER para creación/gestión de eventos sin privilegios de superadministrador.
- Endpoints para iniciar/finalizar eventos controlando habilitación del chat.
- Handler global de excepciones y DTOs para respuestas consistentes.

## Tecnologías Utilizadas
- Java 21, Spring Boot 3
- Módulos: `auth`, `user`, `event`, `registration`, `gamification`, `group`, `chat`, `notification`, `survey`, `integration-googlecalendar`, `security`, `config`, `app`
- PostgreSQL
- WebSocket/STOMP
- Springdoc OpenAPI
 - Testcontainers, GitHub Actions

## Modelo de Entidades
Entidades principales: `User`, `Event`, `Registration`, `Notification`, `Group`, `Survey`, `SurveyQuestion`, `SurveyAnswer`, `Achievement`, `UserAchievement`.

Diagrama ER (alto nivel):
```
User (1) -- (N) Registration (N) -- (1) Event
User (1) -- (N) Notification
User (1) -- (N) UserAchievement (N) -- (1) Achievement
Group (N) -- (N) User
Survey (1) -- (N) SurveyQuestion
SurveyQuestion (1) -- (N) SurveyAnswer (N) -- (1) User
```

## Testing y Manejo de Errores
### Niveles de Testing
- Repositorios: pruebas con Testcontainers (PostgreSQL) en `event`.
- Servicios/Controladores: base preparada para MockMvc y Mockito.

### Resultados
Se validan operaciones básicas de repositorio y se establece la infraestructura para ampliar cobertura.

### Manejo de Errores
- Handler global `config/GlobalExceptionHandler` con `ErrorResponse` consistente (timestamp, status, error, message, path).
- Excepciones personalizadas: `ApiException`, `NotFoundException`, `BadRequestException`, `ForbiddenException`, `UnauthorizedException`, `ConflictException`, `DuplicateResourceException`, `InvalidOperationException`.

## Medidas de Seguridad Implementadas
- Spring Security con `JwtAuthenticationFilter`.
- Rutas públicas: `/api/auth/**`, `/ws/**`, `/actuator/health`.
- Roles: `ADMIN`, `SERVER`, `USER`. Crear eventos y cambiar estado: `ADMIN` o `SERVER`.
 - CORS configurable por `CORS_ALLOWED_ORIGINS`.
 - JWT secrets por variables de entorno.

## Eventos y Asincronía
- WebSocket para notificaciones y chat por evento: `/topic/events.{eventId}`.
- Chat sólo activo cuando el evento está `LIVE`.
- Eventos de aplicación: `RegistrationCreatedEvent` publicado en registro; listener async envía email de confirmación.

## API y Diseño REST
- Convenciones RESTful, versionado implícito, uso de códigos HTTP.
- Ver colección Postman: `postman_collection.json`.

## GitHub & Management
- Uso de ramas para features y PRs; CI con GitHub Actions (`.github/workflows/ci.yml`).

## Deployment
### Ejecución local con Maven
```
./mvnw -q -f univibe-backend/pom.xml clean package
java -jar univibe-backend/app/target/*.jar
```

- Tener servicio de postgresql corriendo (sudo systemctl start postgresql)

### Docker Compose
1. Crear `.env` en la raíz (ejemplo abajo).
2. Construir y levantar (comandos usados por el equipo):
```
docker compose build --no-cache
docker compose up
```
- Tener servicio de postgresql desactivado (sudo systemctl stop postgresql)

3. API disponible en `http://localhost:8080`.

### Swagger / OpenAPI
- Documentación interactiva disponible en:
  - `http://localhost:8080/swagger-ui.html`
  - `http://localhost:8080/swagger-ui/index.html`
  - Espejo JSON: `http://localhost:8080/v3/api-docs`

### Variables de entorno
Configurar en `.env`:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/univibe
SPRING_DATASOURCE_USERNAME=univibe
SPRING_DATASOURCE_PASSWORD=univibe
SERVER_PORT=8080
SECURITY_JWT_SECRET=BASE64_SECRET
SECURITY_JWT_TTL_SECONDS=86400
CORS_ALLOWED_ORIGINS=*
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=your_user
MAIL_PASSWORD=your_pass
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
```

## Conclusiones
Se implementó una arquitectura modular con seguridad robusta, eventos y chat controlados por estado, y documentación completa.

## Licencia
MIT
