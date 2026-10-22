# Task Tracker API

A Spring Boot REST API for managing projects, users, and tasks. It follows the supplied SymphonyAI Media Java Associate assignment and includes an H2 in-memory database, JPA relationships, DTO validation, service layer, structured error handling, unit tests, pagination/filtering, OpenAPI YAML, Swagger UI, and Docker support.

## Tech stack

- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, Bean Validation
- H2 in-memory database
- Maven
- JUnit 5 + Mockito
- OpenAPI 3.0 YAML + springdoc Swagger UI
- Docker / Docker Compose

## Requirements

For local development:
- JDK 17+
- Maven 3.8+ 
- Docker Desktop / Docker Engine only if using Docker

Check versions:

```bash
java -version
mvn -version
docker --version
docker compose version
```

## Run locally

From the project root:

```bash
mvn clean test
mvn spring-boot:run
```

Or package and run the executable JAR:

```bash
mvn clean package
java -jar target/task-tracker-api-1.0.0.jar
```

The API listens on `http://localhost:8080`.

> The required H2 database is in-memory. Data is reset when the application restarts. This is intentional for the assignment's no-external-database requirement.

## Run with Docker

Build and start:

```bash
docker compose up --build -d
```

View logs:

```bash
docker compose logs -f task-tracker-api
```

Stop:

```bash
docker compose down
```

The container exposes port `8080` and does not require Java or Maven installed on the host. The Docker image runs as a non-root user. H2 console is disabled in the Docker Compose configuration.

To use a different host port, change `"8080:8080"` to `"8081:8080"` and call `http://localhost:8081`.

## URLs

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Generated OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Assignment OpenAPI YAML: `openapi.yaml` at the project root
- H2 console (local only): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:tasktracker`
  - User: `user`
  - Password: `user`

## Configuration

All assignment-specific properties are externalised in `src/main/resources/application.properties` and can be overridden by environment variables:

| Property | Environment variable | Default |
|---|---|---|
| `app.name` | `APP_NAME` | `Task Tracker API` |
| `app.version` | `APP_VERSION` | `1.0.0` |
| `task.default-priority` | `TASK_DEFAULT_PRIORITY` | `MEDIUM` |
| `pagination.default-page-size` | `PAGINATION_DEFAULT_PAGE_SIZE` | `20` |
| `server.port` | `SERVER_PORT` | `8080` |

Example:

```bash
APP_VERSION=1.0.1 TASK_DEFAULT_PRIORITY=HIGH SERVER_PORT=8081 mvn spring-boot:run
```

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/info` | App name and version |
| `POST` | `/api/projects` | Create project |
| `GET` | `/api/projects` | List projects |
| `GET` | `/api/projects/{id}` | Get project |
| `PUT` | `/api/projects/{id}` | Update project |
| `DELETE` | `/api/projects/{id}` | Delete project (only if no tasks remain) |
| `GET` | `/api/projects/{id}/tasks` | Paged tasks for a project |
| `POST` | `/api/users` | Create user |
| `GET` | `/api/users` | List users |
| `GET` | `/api/users/{id}` | Get user |
| `PUT` | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user (only if not assigned) |
| `POST` | `/api/tasks` | Create task |
| `GET` | `/api/tasks` | List/filter/page/sort tasks |
| `GET` | `/api/tasks/{id}` | Get task |
| `PUT` | `/api/tasks/{id}` | Update task |
| `DELETE` | `/api/tasks/{id}` | Delete task |

Task query parameters:
- `status`: `TODO`, `IN_PROGRESS`, or `DONE`
- `projectId`: filter by project
- `page`: zero-based page index (default 0)
- `size`: page size (default 20; maximum 100)
- `sortBy`: `id`, `title`, `status`, `priority`, or `dueDate`
- `direction`: `asc` or `desc`

## Sample curl workflow

### 1. Check application info

```bash
curl http://localhost:8080/api/info
```

### 2. Create a project

```bash
curl -i -X POST http://localhost:8080/api/projects \\
  -H "Content-Type: application/json" \\
  -d '{"name":"Payments Platform","description":"Payment services and APIs"}'
```

Copy the returned project `id` for the task request below.

### 3. Create a user

```bash
curl -i -X POST http://localhost:8080/api/users \\
  -H "Content-Type: application/json" \\
  -d '{"name":"Asha Rao","email":"asha@example.com","role":"DEVELOPER"}'
```

Copy the returned user `id`.

### 4. Create a task

Replace `1` with the IDs returned above:

```bash
curl -i -X POST http://localhost:8080/api/tasks \\
  -H "Content-Type: application/json" \\
  -d '{"title":"Implement payment validation","status":"TODO","priority":"HIGH","projectId":1,"assigneeId":1,"dueDate":"2026-11-15"}'
```

`status` and `priority` are optional. Status defaults to `TODO`; priority uses `task.default-priority` (default `MEDIUM`). `assigneeId` and `dueDate` are optional. `projectId` is required and must exist.

### 5. List and filter tasks

```bash
curl "http://localhost:8080/api/tasks?page=0&size=10&sortBy=title&direction=asc"
curl "http://localhost:8080/api/tasks?status=TODO"
curl "http://localhost:8080/api/tasks?projectId=1&status=TODO"
curl "http://localhost:8080/api/projects/1/tasks"
```

### 6. Update task status

`PUT` accepts the task representation. If `status` or `priority` is omitted/null, the existing value is retained for updates. The request still requires `title` and `projectId`.

```bash
curl -i -X PUT http://localhost:8080/api/tasks/1 \\
  -H "Content-Type: application/json" \\
  -d '{"title":"Implement payment validation","status":"IN_PROGRESS","priority":"HIGH","projectId":1,"assigneeId":1,"dueDate":"2026-11-15"}'
```

### 7. Delete a task

```bash
curl -i -X DELETE http://localhost:8080/api/tasks/1
```

## Error response shape

Validation, missing resources, duplicates, and relationship constraints return structured JSON:

```json
{
  "timestamp": "2026-10-22T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/projects",
  "validationErrors": {
    "name": "Project name is required"
  }
}
```

Common status codes:
- `201 Created`: resource created
- `200 OK`: successful read/update
- `204 No Content`: successful delete
- `400 Bad Request`: invalid request body, enum/date, or pagination
- `404 Not Found`: requested resource does not exist
- `409 Conflict`: duplicate unique field or blocked deletion
- `500 Internal Server Error`: unexpected failure (without internal exception details)

## Data model and business rules

- Project has many tasks; every task must reference exactly one existing project.
- A task may have zero or one assignee; an assigned user can have many tasks.
- Project names and user emails are unique; email is normalised to lowercase on write.
- A project cannot be deleted while it has tasks.
- A user cannot be deleted while assigned to tasks. Unassign or delete those tasks first.
- Entities are not returned directly from controllers; DTOs prevent accidental relationship recursion and control the API contract.

## Tests

Run unit tests:

```bash
mvn test
```

Service unit tests use JUnit 5 and Mockito. Add integration tests with `@SpringBootTest` and `MockMvc` if you want to extend the optional bonus section.

