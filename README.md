# API Producer - Messaging Platform

Microservicio REST que recibe solicitudes de envio de mensajes, valida la linea de origen contra MySQL y publica el mensaje en una cola RabbitMQ para su procesamiento asincrono.

## Tecnologias

- Java 21
- Spring Boot 3.4.3
- Spring Security (API Key authentication)
- MySQL 8.0
- RabbitMQ
- Docker

## Prerequisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecucion

No necesitas instalar Java, Maven, MySQL ni ningun otro software. Todo corre dentro de containers Docker.

## Inicio rapido

### 1. Levantar RabbitMQ (servicio compartido)

RabbitMQ es la infraestructura de mensajeria compartida entre ambos microservicios. Debe ejecutarse como un container independiente:

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```

### 2. Levantar el API Producer

```bash
cd api-producer
docker compose up --build -d
```

Esto levanta:
- **MySQL 8.0** con la base de datos `messaging_db` y 5 lineas de origen predefinidas
- **Adminer** (UI web para consultar MySQL)
- **API Producer** (la aplicacion Spring Boot)

> La primera ejecucion tarda unos minutos mientras descarga imagenes y dependencias Maven. Las siguientes son casi instantaneas.

### 3. Verificar que todo esta corriendo

```bash
docker ps
```

Deberias ver 3 containers activos: `api-producer-app`, `api-producer-mysql`, `api-producer-adminer`.

## Servicios disponibles

| Servicio | URL | Descripcion |
|----------|-----|-------------|
| API Producer | http://localhost:8080 | API REST principal |
| Adminer | http://localhost:8082 | UI web para consultar MySQL |
| RabbitMQ Management | http://localhost:15672 | UI de gestion de colas |

### Credenciales

| Servicio | Usuario | Password |
|----------|---------|----------|
| API (header `X-API-KEY`) | - | `my-secret-api-key-2025` |
| Adminer (MySQL) | `messaging_user` | `messaging_pass` |
| RabbitMQ | `guest` | `guest` |

> En Adminer, seleccionar sistema **MySQL**, servidor **mysql**, usuario **messaging_user**, password **messaging_pass**, base de datos **messaging_db**.

## Endpoints

### Enviar mensaje

```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: my-secret-api-key-2025" \
  -d '{
    "origin": "+573001234567",
    "destination": "+573101112222",
    "messageType": "TEXT",
    "content": "Hola, este es un mensaje de prueba"
  }'
```

**Respuesta exitosa (202 Accepted):**
```json
{
  "success": true,
  "message": "Message accepted and queued for processing"
}
```

### Origen invalido

```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: my-secret-api-key-2025" \
  -d '{
    "origin": "+570000000000",
    "destination": "+573101112222",
    "messageType": "TEXT",
    "content": "Este mensaje debe fallar"
  }'
```

**Respuesta (400 Bad Request):**
```json
{
  "success": false,
  "message": "Origin '+570000000000' is not registered in the system"
}
```

### Sin API Key (403 Forbidden)

```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "+573001234567",
    "destination": "+573101112222",
    "messageType": "TEXT",
    "content": "Sin autorizacion"
  }'
```

### Health check

```bash
curl http://localhost:8080/actuator/health
```

## Lineas de origen registradas

La base de datos viene precargada con estas 5 lineas:

| Numero | Descripcion |
|--------|-------------|
| +573001234567 | Origin Line 1 - Main |
| +573009876543 | Origin Line 2 - Secondary |
| +573005551234 | Origin Line 3 - Marketing |
| +573007779999 | Origin Line 4 - Support |
| +573002468135 | Origin Line 5 - Notifications |

## Tipos de mensaje validos

- `TEXT` - Mensaje de texto
- `IMAGE` - Imagen (content = URL)
- `VIDEO` - Video (content = URL)
- `DOCUMENT` - Documento (content = URL)

## Apagar los servicios

```bash
docker compose down

# Para eliminar los datos persistidos:
docker compose down -v
```

## Estructura del proyecto

```
api-producer/
├── src/main/java/com/aldeamo/messaging/apiproducer/
│   ├── config/          # Seguridad (API Key) y RabbitMQ
│   ├── controller/      # REST endpoints
│   ├── dto/             # Request/Response objects
│   ├── entity/          # JPA entities (Origin)
│   ├── exception/       # Manejo centralizado de errores
│   ├── repository/      # Spring Data JPA
│   └── service/         # Logica de negocio
├── init-db/
│   └── init.sql         # Schema y datos iniciales de MySQL
├── Dockerfile           # Multi-stage build (JDK -> JRE Alpine)
├── compose.yaml         # Docker Compose (MySQL + Adminer + App)
└── pom.xml
```
