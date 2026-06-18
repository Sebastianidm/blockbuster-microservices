# eureka-server

`eureka-server` es el registro central de servicios del ecosistema. Su funcion es permitir que `api-gateway` y los microservicios de negocio se descubran dinamicamente sin depender de URLs fijas codificadas.

## Rol dentro del sistema

Este modulo es exclusivamente de infraestructura. No implementa logica de negocio, no expone recursos funcionales del dominio y no tiene persistencia propia del sistema Blockbuster.

Sus responsabilidades son:

- registrar instancias de microservicios
- exponer el estado del registro
- habilitar descubrimiento para gateway y clientes Eureka

## Vista rapida

| Aspecto | Valor |
| --- | --- |
| Puerto | `8761` |
| Persistencia | - |
| Rol | Service discovery |
| Dependencias clave | Eureka Server |

## Comportamiento esperado

Cuando el ecosistema esta levantado, deben registrarse en Eureka:

- `api-gateway`
- `users`
- `catalog`
- `transactions`
- `notifications`

Cada uno debe aparecer con estado `UP` una vez que el contexto del servicio termina de iniciar.

## Endpoints utiles

- `GET /` -> interfaz web de Eureka
- `GET /eureka/apps` -> registro de aplicaciones

Ejemplo local:

```text
http://localhost:8761
http://localhost:8761/eureka/apps
```

## Configuracion

El servidor se configura como standalone:

```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

Eso evita que se intente registrar a si mismo como cliente.

## Ejecucion

Desde este modulo:

```powershell
mvn test
mvn spring-boot:run
```

## Validacion tecnica aplicada

El modulo incluye prueba de arranque de contexto para verificar que:

- la aplicacion compila correctamente
- el contexto de Spring levanta sin requerir un registro externo

## Orden recomendado de arranque

`eureka-server` debe iniciar antes que:

- `api-gateway`
- `ms-users`
- `ms-catalog`
- `ms-transactions`
- `ms-notifications`

## Navegacion

- [README principal](../README.md)
- [api-gateway](../api-gateway/README.md)
- [Guia Postman](../docs/postman/README.md)
