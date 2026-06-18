# api-gateway

`api-gateway` es el punto de entrada unico del ecosistema. Centraliza el acceso externo, enruta solicitudes hacia los microservicios registrados en Eureka y propaga el JWT del cliente hacia los servicios que validan seguridad.

## Rol dentro del sistema

Este modulo no implementa reglas de negocio ni persistencia. Su responsabilidad es de infraestructura:

- exponer un endpoint unico de entrada
- enrutar solicitudes segun la ruta solicitada
- resolver destinos por descubrimiento de servicios
- relayer el header `Authorization` hacia los microservicios protegidos por JWT

## Vista rapida

| Aspecto | Valor |
| --- | --- |
| Puerto | `8080` |
| Persistencia | - |
| Descubrimiento | Eureka client |
| Seguridad propia | no autentica, solo propaga JWT |
| Dependencias clave | Spring Cloud Gateway, Eureka Client |

## Rutas configuradas

| Ruta de entrada | Servicio destino |
| --- | --- |
| `/api/v1/auth/**` | `users` |
| `/api/v1/users/**` | `users` |
| `/api/v1/movies/**` | `catalog` |
| `/api/v1/categories/**` | `catalog` |
| `/api/v1/catalog/**` | `catalog` |
| `/api/v1/rentals/**` | `transactions` |
| `/api/v1/notifications/**` | `notifications` |

## Descubrimiento de servicios

El gateway no usa hosts fijos para los microservicios. Resuelve el destino mediante `lb://<service-name>` y consulta el registro de Eureka para encontrar instancias disponibles.

## Relay de JWT

El filtro `TokenRelayGatewayFilterFactory` toma la cabecera:

```text
Authorization: Bearer <token>
```

y la reenvia al microservicio correspondiente cuando la solicitud pasa por el gateway.

Esto permite que:

- `ms-users` emita el token
- `ms-catalog` y `ms-transactions` lo validen
- el cliente siga consumiendo un unico punto de entrada

## Configuracion

### Local

El gateway usa por defecto:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Docker

Con el perfil `docker`, el registro se resuelve contra:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka
```

## Ejecucion

Desde este modulo:

```powershell
mvn test
mvn spring-boot:run
```

## Validacion tecnica aplicada

El modulo incluye pruebas para:

- arranque del contexto
- propagacion del header `Authorization`
- paso transparente cuando la solicitud no trae Bearer token

## Relacion con el resto del proyecto

En entorno integrado:

- el cliente debe consumir `users`, `catalog` y `transactions` preferentemente a traves de `http://localhost:8080`
- las llamadas internas protegidas por API key pueden seguir apuntando directamente al servicio responsable

## Navegacion

- [README principal](../README.md)
- [eureka-server](../eureka-server/README.md)
- [Guia Postman](../docs/postman/README.md)
