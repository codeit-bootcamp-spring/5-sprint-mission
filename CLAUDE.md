# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Discodeit is a Discord-like messaging application built with Spring Boot 3.4.5 and Java 17. It implements a chat system
with channels (PUBLIC/PRIVATE), messages, users, and file attachments. The project follows a layered architecture with
custom exception handling, comprehensive logging, and monitoring capabilities.

## Build and Development Commands

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application (dev profile by default)
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=prod'

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests UserControllerTest

# Run single test method
./gradlew test --tests UserControllerTest.testGetUser
```

### Code Quality

```bash
# Run checkstyle
./gradlew checkstyle

# Generate test coverage report
./gradlew jacocoTestReport
# Report available at: build/reports/jacoco/test/html/index.html

# Verify coverage meets minimum threshold (60%)
./gradlew jacocoTestCoverageVerification

# Check for dependency updates
./gradlew dependencyUpdates
# Report available at: build/dependencyUpdates/report.json
```

### Database

```bash
# Initialize database schema (manual execution of schema.sql required)
# The application uses schema.sql in src/main/resources/ with ddl-auto: validate
```

## Project Architecture

### Package Structure

```
com.sprint.mission.discodeit/
├── config/               # Configuration classes (JPA, Security, OpenAPI, DataSource proxy)
├── controller/           # REST controllers
│   └── advice/          # Global exception handlers (@RestControllerAdvice)
├── dto/                 # Data Transfer Objects organized by domain
│   ├── user/
│   ├── channel/
│   ├── message/
│   ├── binarycontent/
│   └── response/        # Generic response wrappers (PageResponse)
├── entity/              # JPA entities
│   └── base/           # Base entity classes (BaseEntity, BaseUpdatableEntity)
├── exception/           # Custom exception hierarchy
│   ├── DiscodeitException (base)
│   ├── ErrorCode (enum)
│   └── {domain}/       # Domain-specific exceptions
├── filter/              # Servlet filters
├── logging/             # Logging utilities
├── mapper/              # MapStruct mappers for DTO/Entity conversion
├── repository/          # Spring Data JPA repositories
├── service/             # Business logic layer
├── storage/             # File storage abstraction
└── util/               # Utility classes
```

### Domain Model

**Core Entities:**

- **User**: Users with authentication (bcrypt password hashing)
- **Channel**: Two types - PUBLIC (named channels) and PRIVATE (direct messages)
- **Message**: Messages in channels with optional file attachments
- **BinaryContent**: File storage metadata (actual files stored in .discodeit/storage)
- **MessageAttachment**: Junction table linking messages to files
- **ReadStatus**: Tracks which messages users have read in channels

**Key Relationships:**

- Channels contain many Messages
- Messages have one author (User) and belong to one Channel
- Messages can have multiple BinaryContent attachments (ordered)
- Users have ReadStatus for each Channel tracking last read message
- PRIVATE channels represent direct messaging between users

### Exception Handling

**Exception Hierarchy:**

```
DiscodeitException (base)
├── UserException
│   ├── UserNotFoundException
│   └── DuplicateUserException
├── ChannelException
│   ├── ChannelNotFoundException
│   └── ChannelAccessDeniedException
├── MessageException
│   ├── MessageNotFoundException
│   └── MessageAccessDeniedException
└── BinaryContentException
    └── BinaryContentNotFoundException
```

**ErrorCode Enum:**

- Defines HTTP status codes and error messages for all business exceptions
- Located at: `com.sprint.mission.discodeit.exception.ErrorCode`
- Maps to DiscodeitException via the `errorCode` field

**Global Exception Handler:**

- Implemented using `@RestControllerAdvice` in controller.advice package
- Returns consistent `ErrorResponse` objects with status, exceptionType, message, and details

### Configuration and Profiles

**Profiles:**

- `dev`: Development mode (DEBUG logging, localhost:5432 PostgreSQL, server on 8080)
- `prod`: Production mode (INFO logging, environment-based DB config)
- `test`: Test mode (H2 in-memory DB in PostgreSQL compatibility mode)

**Key Configuration Files:**

- `application.yaml`: Base configuration, defaults to `dev` profile
- `application-local.yaml`: Local-specific settings
- `application-prod.yaml`: Production-specific settings (uses env vars)
- `application-test.yml`: Test configuration with H2 database
- `logback-spring.xml`: Custom logging configuration with daily rolling logs

**Environment Variables (prod):**

- `DB_URL`: PostgreSQL connection URL
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `SPRING_BOOT_ADMIN_CLIENT_URL`: Admin server URL (optional)

### Logging

**Configuration:**

- Uses Logback with custom patterns: `yy-MM-dd HH:mm:ss.SSS [thread] LEVEL logger(36) - message`
- Logs to both console and file (`./.logs/application.log`)
- Daily rolling with 30-day retention
- Profile-specific log levels (DEBUG for dev, INFO for prod)

**DataSource Proxy:**

- Configured via `DataSourceProxyConfig` using `net.ttddyy:datasource-proxy`
- Logs all SQL queries with execution time
- Logs slow queries (>200ms) as warnings
- Query logging level controlled by `net.ttddyy.dsproxy.listener` logger

**Logging Strategy:**

- Service and controller methods log key operations at DEBUG level
- Exceptions logged at ERROR/WARN level
- User actions (create/update/delete) logged with entity IDs
- File operations logged with file metadata

### Storage System

**BinaryContentStorage Interface:**

- Abstraction for file storage with LOCAL and S3 implementations
- Configured via `discodeit.storage.type` (values: `local` or `s3`)
- LOCAL: Files stored in `.discodeit/storage` directory by default
- S3: Files stored in AWS S3 bucket (requires AWS credentials)
- Supports orphan file cleanup with configurable grace period (1h default)

**File Upload:**

- Max file size: 10MB (configurable via `spring.servlet.multipart.max-file-size`)
- Max request size: 30MB (configurable via `spring.servlet.multipart.max-request-size`)
- Metadata stored in `binary_contents` table
- Files identified by UUID

### Testing

**Test Structure:**

```
src/test/java/com/sprint/mission/discodeit/
└── controller/          # WebMvc slice tests (@WebMvcTest)
```

**Test Configuration:**

- H2 in-memory database in PostgreSQL mode
- `ddl-auto: create-drop` for clean state
- Test storage: `./build/test-storage`
- All Actuator endpoints exposed for testing

**Testing Strategy:**

- **Unit Tests**: Service layer with Mockito/BDDMockito for mocking repositories
- **Slice Tests**:
    - Repository tests with `@DataJpaTest` and `@EnableJpaAuditing`
    - Controller tests with `@WebMvcTest` and MockMvc
- **Integration Tests**: `@SpringBootTest` with full context and H2 database
- Use `@Transactional` for test isolation
- Target: 60% code coverage (verified with JaCoCo)

**JaCoCo Coverage:**

- Excludes: config/, dto/, entity/, exception/, mapper/, scheduler/, storage/, util/, *Application.class
- Minimum requirement: 60% line coverage for service layer
- Run `./gradlew jacocoTestReport` to generate reports

### Monitoring and Actuator

**Exposed Endpoints:**

- `/actuator/health`: Application health status
- `/actuator/info`: Application metadata (name, version, Java version, config info)
- `/actuator/metrics`: Application metrics
- `/actuator/loggers`: Runtime logger configuration

**Spring Boot Admin Integration:**

- Admin server runs on port 9090 (separate module)
- Main application registers as client to admin server
- Provides enhanced visualization of metrics and logs

### API Documentation

**OpenAPI/Swagger:**

- API docs: `http://localhost:8080/docs`
- Swagger UI: `http://localhost:8080/docs/ui`
- Configured via SpringDoc (`springdoc-openapi-starter-webmvc-ui`)
- Enabled in dev/prod, disabled in test profile

### Security

**Password Handling:**

- BCrypt password encoder (`org.springframework.security:spring-security-crypto`)
- Hash strength not specified in config (uses Spring default: 10 rounds)

**Authentication:**

- Basic authentication support via `AuthService` and `AuthController`
- No JWT/session management visible in configuration

### Utilities and Tooling

**MapStruct:**

- Automatic DTO/Entity mapping
- Mappers located in `mapper/` package
- Lombok binding configured via `lombok-mapstruct-binding`

**Checkstyle:**

- Configuration: `config/checkstyle/checkstyle.xml`
- Suppressions: `config/checkstyle/checkstyle-suppressions.xml`
- Zero warnings policy (`maxWarnings = 0`)

**Gradle Versions Plugin:**

- Check updates with `./gradlew dependencyUpdates`
- Rejects unstable versions (alpha, beta, RC)
- Report in JSON format

## Important Implementation Notes

### When Adding New Features

1. **Custom Exceptions**:
    - Create domain-specific exception extending appropriate base (UserException, ChannelException, etc.)
    - Add new ErrorCode entries with HTTP status and message
    - Handle in GlobalExceptionHandler with consistent ErrorResponse format

2. **DTOs and Entities**:
    - Use MapStruct for conversions (create mapper interface)
    - Entities extend BaseEntity or BaseUpdatableEntity for audit fields
    - DTOs organized by domain in separate packages

3. **Logging**:
    - Use `@Slf4j` annotation on classes
    - Log service entry/exit with DEBUG level
    - Log business exceptions with ERROR/WARN level including entity IDs
    - Include relevant context (userId, channelId, messageId) in log messages

4. **Testing**:
    - Minimum 2 test cases per method (success + failure scenarios)
    - Use BDDMockito for readable test structure (given/when/then)
    - Import necessary beans for WebMvcTest using `@Import` annotation
    - Ensure 60% minimum code coverage for service layer

5. **File Handling**:
    - Always use BinaryContentStorage abstraction
    - Store metadata in database via BinaryContentService
    - Clean up orphaned files via scheduled task
    - Validate file size/type at controller layer

### Database Schema Management

- Schema defined in `src/main/resources/schema.sql`
- JPA validation mode: `ddl-auto: validate` (production-safe)
- Manual migrations required (no Flyway/Liquibase)
- Test profile uses `create-drop` for clean test state

### Performance Considerations

- Hibernate batch size: 20 queries
- Default fetch batch size: 100 entities
- Order inserts/updates enabled for better batching
- Open-in-view disabled (explicit transaction boundaries)
- Server compression enabled for text/JSON responses

### Multipart Form Handling

- Users and Messages support multipart forms for file uploads
- Pattern: `{Entity}CreateMultipartForm` and `{Entity}UpdateMultipartForm` DTOs
- Files uploaded via `MultipartFile` parameters
- Metadata JSON + file(s) in single request
