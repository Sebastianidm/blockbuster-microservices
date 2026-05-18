# Postman Local

Esta carpeta contiene la coleccion y el environment necesarios para validar localmente el flujo integrado de los cuatro microservicios del proyecto:

- `ms-users`
- `ms-catalog`
- `ms-transactions`
- `ms-notifications`

## Archivos

- [Collection](./Blockbuster-system-integration.postman_collection.json)
- [Environment local](./Blockbuster-local.postman_environment.json)

## Uso recomendado

1. Importar ambos archivos en Postman.
2. Seleccionar el environment `Blockbuster Local`.
3. Verificar que `internal_api_key` coincida con la configuracion local.
4. Levantar localmente los cuatro microservicios.
5. Ejecutar las carpetas en este orden:
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

## Flujos cubiertos

- login de administrador semilla
- registro de usuario demo
- login de usuario demo
- consulta protegida de usuarios
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

## Navegacion

- [README principal](../../README.md)
- [ms-users](../../users/users/README.md)
- [ms-catalog](../../catalog/catalog/README.md)
- [ms-transactions](../../transactions/transactions/README.md)
- [ms-notifications](../../notifications/notifications/README.md)
