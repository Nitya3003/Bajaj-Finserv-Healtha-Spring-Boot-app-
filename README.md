
# BFHL Java Qualifier – Spring Boot

Implements the flow described in the assignment:

- On startup, **POST** to `/hiring/generateWebhook/JAVA` with your details.
- Receives `webhook` and `accessToken`.
- Chooses the question by **parity of last two digits of `regNo`** and obtains a **final SQL query**.
- Stores the result locally in an **H2** database.
- Submits the `finalQuery` using **JWT in the `Authorization` header** to `/hiring/testWebhook/JAVA`.

## Quick Start

1. Edit `src/main/resources/application.yml` and set your **name**, **regNo**, **email**.
2. Build:
   ```bash
   ./mvnw -q -DskipTests package
   ```
3. Run:
   ```bash
   java -jar target/bfhl-java-qualifier-springboot-0.0.1-SNAPSHOT.jar
   ```

> ⚠️ The default `SqlSolver` returns placeholder queries (`SELECT 1;` or `SELECT 2;`). Replace this with your **actual SQL** per your assigned question before running a real submission.

## Where to change the solution?

- Edit: `src/main/java/com/example/bfhl/sql/SqlSolver.java`

## Tech

- Java 17, Spring Boot 3 (WebFlux), H2, JPA
- Uses **WebClient** and runs the flow on **application ready** (no controller endpoints involved).

## Notes

- Authorization header is set exactly to the provided `accessToken` (no `Bearer` prefix), as required.
- The question URLs used to decide which problem you have are embedded in `WebhookClient#questionUrlFor(boolean odd)` for convenience.

