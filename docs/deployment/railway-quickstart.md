# Railway Quickstart

Esta guía deja el despliegue remoto lo más simple posible, sin alterar el flujo local del proyecto.

## Objetivo

Desplegar en Railway:

- `eureka-server`
- `api-gateway`
- `users`
- `catalog`
- `transactions`
- `notifications`

Manteniendo:

- PostgreSQL en Neon
- MongoDB Atlas

## Cambios mínimos ya considerados

Los servicios ya quedaron preparados para:

- usar `PORT` dinámico en cloud
- seguir usando `SERVER_PORT` en local
- resolver `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` por variable de entorno

## Orden recomendado de creación en Railway

1. `eureka-server`
2. `notifications`
3. `users`
4. `catalog`
5. `transactions`
6. `api-gateway`

## Tipo de despliegue recomendado

Crear un servicio por carpeta usando el `Dockerfile` ya incluido en cada módulo.

## Root directory por servicio

- `eureka-server`
- `api-gateway`
- `notifications/notifications`
- `users/users`
- `catalog/catalog`
- `transactions/transactions`

## Variables compartidas

Estas variables deben repetirse en los servicios que correspondan:

- `DB_USERNAME`
- `DB_PASSWORD`
- `MONGO_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `INTERNAL_API_KEY`

## Variable clave para Eureka

En `api-gateway`, `users`, `catalog`, `transactions` y `notifications`:

```text
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server.railway.internal/eureka
```

## Variables de URLs internas

### users

```text
NOTIFICATIONS_SERVICE_URL=http://notifications.railway.internal
```

### transactions

```text
USERS_SERVICE_URL=http://users.railway.internal
CATALOG_SERVICE_URL=http://catalog.railway.internal
NOTIFICATIONS_SERVICE_URL=http://notifications.railway.internal
```

## Visibilidad sugerida

### Públicos

- `api-gateway`
- opcionalmente `eureka-server` solo si quieres verlo desde fuera

### Privados

- `users`
- `catalog`
- `transactions`
- `notifications`

## Endpoints útiles post-despliegue

### Eureka

- `https://<eureka-public-url>/`

### Gateway

- `https://<gateway-public-url>/api/v1/auth/login`
- `https://<gateway-public-url>/api/v1/movies/available`
- `https://<gateway-public-url>/api/v1/rentals`

## Validación mínima

1. Confirmar que `eureka-server` quedó arriba.
2. Confirmar que los demás servicios arrancan sin error.
3. Verificar en Eureka que todos estén registrados.
4. Probar login por gateway.
5. Probar listado de películas por gateway.
6. Probar creación de arriendo por gateway.

## Si un servicio falla al iniciar

Revisar primero:

- variable `PORT`
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
- credenciales de Neon o Mongo
- variables compartidas de JWT y API key

## Nota práctica

Si Railway solicita healthcheck HTTP y quieres evitar cambios extra de código, comienza sin healthcheck personalizado o usa uno que ya responda `200` en tu servicio. Si más adelante hace falta, conviene añadir una ruta de health dedicada en una iteración separada.
