# Render Quickstart

Esta guía deja preparado el despliegue en Render con el menor impacto posible sobre el flujo local y Docker.

## Objetivo

Desplegar en una misma Blueprint:

- `eureka-server`
- `api-gateway`
- `users`
- `catalog`
- `transactions`
- `notifications`

Manteniendo:

- PostgreSQL en Neon
- MongoDB Atlas
- Eureka visible para revisión y demostración
- API Gateway como único punto de entrada público

## Qué agrega esta preparación

- soporte de `PORT` dinámico para cloud
- `render.yaml` en la raíz del repositorio
- perfil `render` en `api-gateway` para enrutar por direcciones internas de Render
- endpoint local `GET /health` en `api-gateway` para healthcheck estable

## Estrategia usada

Render puede desplegar todos los servicios del monorepo por separado usando sus `Dockerfile`.

Para reducir riesgo:

- los microservicios siguen registrándose en Eureka
- el `api-gateway` usa rutas HTTP internas directas en perfil `render`
- `transactions` y `users` siguen resolviendo URLs internas por variables de entorno

Así se conserva Eureka para la demo, pero el enrutamiento crítico no depende del balanceo `lb://`.

## Servicios públicos y privados

### Públicos

- `api-gateway`
- `eureka-server`

### Privados

- `users`
- `catalog`
- `transactions`
- `notifications`

## Archivo clave

Render detectará el archivo:

```text
render.yaml
```

Desde ahí podrá crear los 6 servicios del proyecto.

## Variables sensibles

Render pedirá completar manualmente los valores marcados con `sync: false`, principalmente:

- `DB_PASSWORD`
- `MONGO_PASSWORD`
- `JWT_SECRET`
- `INTERNAL_API_KEY`

## Variables ya resueltas por Blueprint

La Blueprint deja enlazadas automáticamente:

- las rutas internas entre servicios
- el `host:port` interno de Eureka para registro
- los `host:port` privados usados por `transactions`, `users` y `api-gateway`

## Flujo recomendado en Render

1. Crear un nuevo Blueprint Service desde el repositorio.
2. Seleccionar la rama que quieras desplegar.
3. Confirmar que Render detecte `render.yaml`.
4. Completar solo los secretos pendientes.
5. Lanzar el despliegue.
6. Esperar primero que `eureka-server` quede sano.
7. Verificar luego `notifications`, `users`, `catalog`, `transactions` y `api-gateway`.

## Validación mínima post despliegue

### Eureka

- abrir la URL pública de `eureka-server`
- verificar que aparezcan `API-GATEWAY`, `USERS`, `CATALOG`, `TRANSACTIONS` y `NOTIFICATIONS`

### Gateway

- `POST /api/v1/auth/login`
- `GET /api/v1/movies/available`
- `POST /api/v1/rentals`
- `PUT /api/v1/rentals/{id}/return`

## Si algo falla

Revisar en este orden:

1. que todos los servicios tengan `PORT=10000`
2. que los secretos sensibles estén cargados correctamente
3. que `eureka-server` haya iniciado antes que el resto
4. que `api-gateway` responda `200` en `/health`
5. que cada microservicio responda `200` en `/v3/api-docs`

## Impacto sobre local

Estos cambios no reemplazan el flujo actual:

- `mvn spring-boot:run` local sigue funcionando
- `docker-compose.yml` sigue siendo válido
- Railway puede seguir usándose como alternativa si se mantiene el perfil correspondiente
