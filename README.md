# dontsweat-api

Spring Boot backend for Dontsweat.

This service is intended to own backend domain logic, persistence, validation, and API behavior. The Next.js frontend can keep TypeScript routing as a thin proxy or frontend-facing BFF layer, but the Java API should become the source of truth for database writes and domain rules.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- Flyway
- PostgreSQL
- Maven
- Spotless with google-java-format

## Local Development

Start the API:

```bash
./mvnw spring-boot:run
```

The default API port is `8080`.

Database settings are configured in:

```txt
src/main/resources/application.yml
```

Use environment variables for machine-specific values:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/dontsweat
DATABASE_USERNAME=dontsweat
DATABASE_PASSWORD=dontsweat
```

## Formatting

Check formatting:

```bash
./mvnw spotless:check
```

Apply formatting:

```bash
./mvnw spotless:apply
```

The project uses Spotless and google-java-format. Editor defaults are also checked in through `.editorconfig` and `.vscode/settings.json`.

## Project Structure

Organize code by domain package:

```txt
src/main/java/com/dontsweat/api/
  common/
  orgs/
  users/
  memberships/
  clients/
  templates/
  sessions/
```

Each domain package should generally contain:

```txt
Entity
Repository
Service
Controller
Request DTOs
Response DTOs
```

Keep these responsibilities separate:

- DTOs define API input and output shapes.
- Entities define persistence shape.
- Services own business rules and transactions.
- Controllers translate HTTP requests into service calls.

## Schema

This schema is intentionally designed with **clear separation of concerns** between identity, tenancy, access control, and domain data. The goal is to support a real multi-tenant training system while preserving immutable workout history and allowing future expansion such as client logins, multiple trainers, and analytics.

### Core Principles

1. **All data belongs to a tenant (`Organization`).**
   - The organization is the primary data boundary. Clients, templates, and sessions are always scoped to an org.

2. **Identity is separate from domain data.**
   - A `User` represents a person who can log in.
   - A `Client` represents a training record and may exist without a login.

3. **Access control is modeled explicitly via a join table (`Membership`).**
   - Roles are not stored on users directly because roles depend on the organization context.

4. **Workout history is immutable.**
   - Templates define plans. Assignments copy those plans into session instances so past workouts never change when templates are edited.

5. **Public access is capability-based.**
   - Share links use unguessable tokens and grant read-only access without authentication.

### Entity Responsibilities

#### Identity

- **`User`**
  - Represents a person who can authenticate.
  - Stores identity data only, such as email and name.
  - Can belong to multiple organizations.
  - May optionally be linked to a client record.

#### Tenant

- **`Organization`**
  - Represents a tenant, gym, or training group.
  - Owns all domain data, including clients, templates, and sessions.
  - Serves as the primary data partition.

#### Access Control

- **`Membership`**
  - Join table between `User` and `Organization`.
  - Stores role information: `OWNER`, `TRAINER`, `STAFF`, `CLIENT`.
  - A single user can hold multiple roles within the same organization.
  - Enables scenarios where a user is both a trainer and a client.

#### Domain Actor

- **`Client`**
  - Represents a person being trained.
  - Always belongs to an organization.
  - Can exist without a login.
  - Can optionally be linked to a `User` account.
  - Supports archiving instead of deletion.

#### Plans

- **`SessionTemplate`**
  - Defines a reusable workout plan.
  - Owned by an organization.

- **`TemplateExercise`**
  - Exercise rows that belong to a template.
  - Ordered explicitly via `sortOrder`.

#### History

- **`SessionInstance`**
  - A scheduled instance of a workout for a client on a specific date.
  - Optionally references the originating template.
  - Preserved even if the template is deleted.

- **`SessionExercise`**
  - A snapshot copy of exercises at assignment time.
  - Stores both planned values and actual results.
  - Ensures workout history never mutates.

### Design Guarantees

- **Multi-tenant safe**: All domain data is scoped to an organization.
- **Role-flexible**: Users can have multiple roles per organization.
- **Client-login optional**: Clients do not require accounts.
- **History-safe**: Editing templates never affects past sessions.
- **Future-proof**: Supports client portals, multiple trainers, analytics, and auditing.

### Constraints & Indexing

- Composite uniqueness is used where tenancy matters, such as client-to-user links per organization.
- Foreign keys are indexed for common access patterns.
- Cascade deletes are used for true ownership.
- Nulling foreign keys is used where history must be preserved.
- Share links are enforced via unique, unguessable tokens.

## Migration Order

Migrate the existing backend model into Java in dependency order:

1. `Organization`
2. `User`
3. `Membership`
4. `Client`
5. `SessionTemplate`
6. `TemplateExercise`
7. `SessionInstance`
8. `SessionExercise`
9. Public share-token access

After each model is migrated, update the Next.js route for that resource to proxy to the Java API instead of writing through Prisma.
