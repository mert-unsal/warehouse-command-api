# Warehouse Command API

A Spring Boot write-side (command) service for managing warehouse domain entities: Articles (inventory items) and Products (assemblies of articles). It focuses on validated state changes, optimistic locking, and clean separation from read/query concerns (CQRS style). OpenAPI & Actuator are exposed for integration and ops.

## Architecture Overview
This service provides REST endpoints to create and mutate:
- Articles: id (string), name, stock quantity
- Products: product definition composed of required article amounts

Design highlights:
- Command-side only (no query aggregation endpoints here)
- Optimistic locking via `@Version` on MongoDB documents
- Validation layer using Jakarta Bean Validation annotations on request DTOs
- Retry semantics on service methods (Spring Retry) for transient failures
- Factory classes (manual mapping) instead of generated mappers (MapStruct dependency present for future adoption)
- MongoDB persistence with helpful indexes (article name index; compound index on `products.containArticles.artId`)
- Structured logging (JSON) and OpenTelemetry auto instrumentation hooks prepared (exporters disabled by default)

## Responsibilities
- Create & update Articles (name, stock)
- Atomically decrease Article stock (with optimistic locking & safe decrement repository method)
- Create & update Products (definition + article requirements)
- Emit structured logs & telemetry context (trace/span IDs in MDC)
- Expose OpenAPI & health endpoints

## Tech Stack
- Java 21, Spring Boot 3.5.5
- Spring Web, Spring Data MongoDB
- Spring Retry + AOP
- Bean Validation (Jakarta / Hibernate Validator)
- (Planned) MapStruct – dependency present, not yet used (manual factories currently)
- springdoc-openapi (OpenAPI 3) UI
- Actuator (health/info/loggers/env)
- OpenTelemetry instrumentation (starter + logback MDC)
- Logstash Logback Encoder (JSON logging)
- Docker (multi-stage image)

## Domain Models (MongoDB Collections)
Articles (`articles`):
```
{ id, name, stock, version, createdDate, lastModifiedDate, fileCreatedAt }
```
Products (`products`):
```
{ id (ObjectId), name, containArticles[ { artId, amountOf } ], version, createdDate, lastModifiedDate, fileCreatedAt }
```
Indexes:
- `articles.name` (simple @Indexed)
- `products.containArticles.artId` (compound index for nested array lookup)

## Request / Response Data Shapes
ArticleCommandRequest (JSON):
```
{
  "article_id": "A001",
  "name": "table leg",
  "stock": 50
}
```
ProductCommandRequest (JSON):
```
{
  "name": "Dining Table",
  "containArticles": [
    { "art_id": "A001", "amount_of": 4 },
    { "art_id": "A002", "amount_of": 1 }
  ]
}
```
Note: Inner objects use snake_case (`art_id`, `amount_of`) mapped to `ArticleAmount(artId, amountOf)`.

## REST API Endpoints
Base paths:
- Articles: `/api/v1/commands/articles`
- Products: `/api/v1/commands/products`

Articles:
- `POST /` – Create article
  - Body: ArticleCommandRequest
  - 201 -> ArticleResponse
- `PATCH /{id}` – Partial update (name and/or stock). Fields omitted remain unchanged.
- `POST /{id}/decrease` – Decrease stock by provided `stock` value
  - Body must include `stock` (required); `name` & `article_id` ignored for the operation, only used for validation context.

Products:
- `POST /` – Create product definition
- `PUT /{id}` – Full replacement update (name + containArticles list)

Swagger UI: `http://localhost:8082/swagger-ui.html`
OpenAPI JSON: `http://localhost:8082/api-docs`

## Validation & Concurrency
Articles:
- `stock >= 1` when provided (creation & decrease)
- Decrease requires a positive `stock` value representing decrement amount
Products:
- `name`: not blank
- `containArticles`: non-empty list
- Each `ArticleAmount`: `art_id` non-empty, `amount_of >= 1`
Concurrency:
- `@Version` field on documents enforces optimistic locking. Conflicts return HTTP 409 (OptimisticLockingFailureException mapped).

## Error Handling
GlobalExceptionHandler maps exceptions to structured JSON:
```
{
  "error": "OPTIMISTIC_LOCK_CONFLICT",
  "message": "...",
  "status": 409,
  "path": "/api/v1/commands/articles/A001",
  "timestamp": "2025-09-05T12:00:00"
}
```
Key error codes:
- `INVALID_JSON_FORMAT`
- `UNSUPPORTED_MEDIA_TYPE`
- `FILE_PROCESSING_ERROR` (validation grouping message – naming retained from shared utilities)
- `INVALID_ARGUMENT`
- `NOT_FOUND`
- `OPTIMISTIC_LOCK_CONFLICT`
- `INTERNAL_SERVER_ERROR`

Validation failures differentiate inventory vs product paths to produce tailored messages.

## Configuration & Profiles
Active profiles default (application.yaml): `logging,management,mongo`
Profiles:
- `mongo` – MongoDB connection (URI-based)
- `logging` – log levels / JSON formatting
- `management` – actuator exposure

Environment overrides:
```
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/ikea
MONGODB_DATABASE=ikea

# Server
PORT=8082   # or SERVER_PORT

# Logging
LOG_LEVEL_ROOT=INFO
```
OpenTelemetry (disabled exporters by default) can be enabled by supplying OTEL_* env vars (see `application.yaml`).

## Running Locally
Prerequisites: Java 21+, Maven 3.9+, Docker (optional for container run), MongoDB instance.

Maven run (with default profiles):
```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=logging,management,mongo
```
Build JAR & run:
```bash
mvn clean package
java --enable-preview -jar target/warehouse-command-api-0.0.1-SNAPSHOT.jar
```
Override port:
```bash
PORT=9090 java -jar target/warehouse-command-api-0.0.1-SNAPSHOT.jar
```

## Docker
Build image:
```bash
docker build -t ikea/warehouse-command-api ./warehouse-command-api
```
(If part of root composition, ensure MongoDB is available or override URI for remote cluster.)

## Sample cURL Calls
Create Article:
```bash
curl -X POST http://localhost:8082/api/v1/commands/articles \
  -H 'Content-Type: application/json' \
  -d '{"article_id":"A001","name":"leg","stock":12}'
```
Decrease Stock:
```bash
curl -X POST http://localhost:8082/api/v1/commands/articles/A001/decrease \
  -H 'Content-Type: application/json' \
  -d '{"article_id":"A001","stock":3}'
```
Create Product:
```bash
curl -X POST http://localhost:8082/api/v1/commands/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Dining Chair","containArticles":[{"art_id":"A001","amount_of":4},{"art_id":"A002","amount_of":1}]}'
```

## Observability & Logging
- JSON logs via logstash encoder (add an appender config for external sinks)
- Trace & span context auto-injected when OTEL exporters enabled
- Actuator health: `GET /actuator/health`
- Additional exposed endpoints: info, env, loggers, configprops (see management profile)

## Testing
Run unit tests:
```bash
mvn test
```
