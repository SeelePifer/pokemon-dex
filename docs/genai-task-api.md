# Generative AI exercise: task API

This is the written exercise from the interview brief. It is not a second running application. The prompt below is what I would give a coding assistant. The sample is representative output. The last section is the review I would do before trusting it.

## Prompt

```text
Build a Java 21 Spring Boot 3 REST API for personal tasks. Use a hexagonal layout:
domain and application must not import Spring, JPA, or web types.

A task has a UUID id, title, description, status (TODO, IN_PROGRESS, DONE),
due date, and the UUID of the owner. A user already exists and is identified
by the authenticated principal. Do not accept the owner id from the client.

Endpoints, all authenticated:
- POST /api/v1/tasks creates a task for the current user
- GET /api/v1/tasks?status=&page=&size= lists only the current user's tasks
- GET /api/v1/tasks/{id} returns 404 when the id is missing OR belongs to someone else
- PUT /api/v1/tasks/{id} updates title, description, status, and due date
- DELETE /api/v1/tasks/{id} returns 204, or 404 when the caller does not own it

Validation:
- title is required, trimmed, max 120
- description max 2000, empty allowed
- due date is required and must be today or later on create
- status is required and must be one of the enum values
- page >= 0, size 1..50

Return one error JSON shape: timestamp, status, error, message, path, fieldErrors.
Use 400 for malformed bodies, 401 when there is no principal, 404 for a missing
or foreign task. Write JUnit tests for the use cases with a fake repository
before the controller. Do not add Lombok. Use records for commands and responses.
```

## Representative sample

This is the kind of controller and use case the prompt is meant to produce. I would still read it line by line.

```java
public record CreateTask(String title, String description, TaskStatus status, LocalDate dueDate) {}

public final class CreateTaskUseCase {
    private final TaskRepository tasks;
    private final Clock clock;

    public CreateTaskUseCase(TaskRepository tasks, Clock clock) {
        this.tasks = tasks;
        this.clock = clock;
    }

    public Task execute(UUID ownerId, CreateTask command) {
        String title = requireTitle(command.title());
        if (command.dueDate() == null || command.dueDate().isBefore(LocalDate.now(clock))) {
            throw new InvalidRequestException("Due date must be today or later");
        }
        if (command.status() == null) {
            throw new InvalidRequestException("Status is required");
        }
        return tasks.save(Task.create(ownerId, title, clean(command.description()), command.status(), command.dueDate()));
    }
}

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController {
    private final CreateTaskUseCase createTask;

    TaskController(CreateTaskUseCase createTask) {
        this.createTask = createTask;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return TaskResponse.from(createTask.execute(CurrentUser.require().id(), request.toCommand()));
    }
}
```

## How I validated it

- I ran the use-case tests first, with a fake repository, and checked the assertions rather than only the green bar. A test that never sets a foreign owner does not prove the 404 rule.
- I checked that the controller takes the owner from the security principal. If the generated request DTO still has `userId`, I delete that field.
- I tried an empty title, a past due date, a task id that exists for another user, and a missing token. I expect 400, 400, 404, and 401 in that order.
- I confirmed the repository query filters by owner. Hiding the row only in the controller is not enough, because another use case would forget.

## What I would correct

- Ownership stays in the use case. Returning 403 for a foreign id also leaks that the id exists, so the prompt asks for 404.
- Due-date checks use an injected `Clock`. `LocalDate.now()` makes the test depend on the machine date.
- Status is an enum. A free string would accept `"done "` and skip the state machine later.
- Bean validation on the request DTO and the same rules in the use case are both kept. The web layer produces `fieldErrors`. The use case still protects a non-HTTP caller.
- Pagination is bounded. An unbounded `findAll` is the first thing I remove from generated code.
- I would add an index on `(owner_id, due_date)` once the query is real, and I would not log the task description if it can hold private notes.

Authentication is the existing JWT filter from this project. The task API would reuse it instead of inventing a second login.
