# Postman Local

Esta carpeta contiene la coleccion y el environment para validar el ecosistema completo de Blockbuster en entorno local.

La coleccion combina dos tipos de pruebas:

- flujo de cliente final a traves de `api-gateway`
- integraciones internas directas hacia endpoints protegidos por API key

## Archivos

- [Collection](./Blockbuster-system-integration.postman_collection.json)
- [Environment local](./Blockbuster-local.postman_environment.json)

## Variables esperadas

- `gateway_url=http://localhost:8080`
- `eureka_url=http://localhost:8761`
- `users_url=http://localhost:8082`
- `catalog_url=http://localhost:8081`
- `transactions_url=http://localhost:8083`
- `notifications_url=http://localhost:8084`
- `internal_api_key=<shared-key>`

El environment tambien almacena:

- `admin_token`
- `employee_token`
- `user_token`
- `admin_user_id`
- `user_id`
- `category_id`
- `movie_id`
- `rental_id`

## Uso recomendado

1. Importar la coleccion y el environment.
2. Seleccionar `Blockbuster Local`.
3. Completar `internal_api_key` si corresponde.
4. Levantar localmente `eureka-server`, `api-gateway` y los cuatro microservicios de negocio.
5. Ejecutar las carpetas en este orden:
   - `00 Infraestructura`
   - `01 Auth`
   - `02 Users`
   - `03 Catalog`
   - `04 Notifications`
   - `05 Transactions`
   - `06 Negative Tests`

## Cobertura del flujo

### Infraestructura

- consulta del registro en Eureka

### Auth y Users

- login admin semilla
- login empleado semilla
- registro de usuario demo
- login de usuario demo
- consulta protegida de usuarios
- consulta interna por API key

### Catalog

- creacion de categoria
- listado de categorias via gateway
- creacion de pelicula
- listado de peliculas disponibles via gateway
- descuento interno de stock
- reintegro interno de stock

### Notifications

- envio manual de notificacion por API key

### Transactions

- creacion de arriendo
- consulta de arriendos por usuario
- consulta global de arriendos
- devolucion por `PATCH`
- eliminacion administrativa

### Negative tests

- acceso prohibido por rol
- rechazo de endpoint interno sin API key
- rechazo de arriendo con `userId` ajeno
- rechazo de notificaciones sin API key

## Criterio de consumo

Usar `gateway_url` para:

- `auth`
- `users` de consumo cliente
- `catalog` de consumo cliente
- `transactions`

Usar URLs directas por servicio para:

- endpoints internos con `X-Internal-Api-Key`
- consultas explicitas de infraestructura

## Navegacion

- [README principal](../../README.md)
- [api-gateway](../../api-gateway/README.md)
- [eureka-server](../../eureka-server/README.md)
- [ms-users](../../users/users/README.md)
- [ms-catalog](../../catalog/catalog/README.md)
- [ms-transactions](../../transactions/transactions/README.md)
- [ms-notifications](../../notifications/notifications/README.md)
