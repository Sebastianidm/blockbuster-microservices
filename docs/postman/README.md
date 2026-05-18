# Postman Local

Esta carpeta contiene una coleccion y un environment para validar localmente los 4 microservicios del proyecto Blockbuster:

- `ms-catalog`
- `ms-users`
- `ms-transactions`
- `ms-notifications`

## Archivos

- `Blockbuster-system-integration.postman_collection.json`
- `Blockbuster-local.postman_environment.json`

## Orden de uso

1. Importa ambos archivos en Postman.
2. Selecciona el environment `Blockbuster Local`.
3. Ajusta el valor de `internal_api_key` para que coincida con tus `.env`.
4. Levanta localmente los 4 microservicios.
5. Ejecuta las carpetas en este orden:
   - `01 Auth`
   - `02 Users`
   - `03 Catalog`
   - `04 Notifications`
   - `05 Transactions`
   - `06 Negative Tests`

## Variables esperadas

- `users_url=http://localhost:8082`
- `catalog_url=http://localhost:8081`
- `transactions_url=http://localhost:8083`
- `notifications_url=http://localhost:8084`
- `internal_api_key=<shared-key>`

La coleccion guarda automaticamente:

- `admin_token`
- `user_token`
- `admin_user_id`
- `user_id`
- `category_id`
- `movie_id`
- `rental_id`

## Flujo cubierto

- login de administrador semilla
- registro de usuario demo
- login de usuario demo
- consulta de usuarios protegida
- consulta interna por API key
- creacion de categoria y pelicula
- consulta de catalogo
- descuento interno de stock
- reintegro interno de stock
- envio directo de notificacion
- creacion de arriendo
- consulta de arriendos por usuario
- consulta administrativa de todos los arriendos
- devolucion del arriendo por `PATCH`
- eliminacion administrativa del arriendo
- pruebas negativas de JWT y API key
