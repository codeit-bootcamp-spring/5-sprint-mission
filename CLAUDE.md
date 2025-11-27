# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Discodeit is a Discord-like messaging application built with Spring Boot 3.4.5 and Java 17. It implements a chat system
with channels (PUBLIC/PRIVATE), messages, users, and file attachments. The project follows a layered architecture with
JWT-based authentication, role-based access control (RBAC), custom exception handling, comprehensive logging, and
monitoring capabilities.

## Build and Development Commands

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application (local profile by default)
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
├── config/               # Configuration classes (JPA, Security, OpenAPI, DataSource proxy, AWS S3)
│   └── properties/      # @ConfigurationProperties classes
├── controller/           # REST controllers
├── docs/                # OpenAPI documentation interfaces
├── dto/                 # Data Transfer Objects organized by domain
│   ├── user/
│   ├── channel/
│   ├── message/
│   ├── binarycontent/
│   ├── readstatus/
│   ├── data/            # JWT-related DTOs
│   ├── request/         # Generic request DTOs
│   └── response/        # Generic response wrappers (PageResponse)
├── entity/              # JPA entities
│   └── base/           # Base entity classes (BaseEntity, BaseUpdatableEntity)
├── exception/           # Custom exception hierarchy
│   ├── DiscodeitException (base)
│   ├── ErrorCode (enum)
│   ├── ErrorResponse
│   ├── GlobalExceptionHandler
│   └── {domain}/       # Domain-specific exceptions
├── mapper/              # MapStruct mappers for DTO/Entity conversion
├── repository/          # Spring Data JPA repositories
├── scheduler/           # Scheduled tasks (FileCleanupScheduler)
├── security/            # Security components
│   ├── jwt/            # JWT token handling (provider, filters, handlers)
│   └── audit/          # Authentication audit and metrics
├── service/             # Business logic layer
├── storage/             # File storage abstraction
│   └── local/          # Local storage implementation
└── util/               # Utility classes
```

### Domain Model

**Core Entities:**

- **User**: Users with authentication (bcrypt password hashing), profile images, and roles (ADMIN, CHANNEL_MANAGER, USER)
- **Channel**: Two types - PUBLIC (named channels) and PRIVATE (direct messages)
- **Message**: Messages in channels with optional file attachments
- **BinaryContent**: File storage metadata (actual files stored in .discodeit/storage or S3)
- **MessageAttachment**: Junction table linking messages to files (composite key via MessageAttachmentId)
- **ReadStatus**: Tracks which messages users have read in channels
- **AuthAuditLog**: Tracks authentication events for security auditing
- **Role**: Enum defining user roles with hierarchy (ADMIN > CHANNEL_MANAGER > USER)

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
├── AuthException
│   ├── InvalidCredentialsException
│   ├── InvalidTokenException
│   └── InsufficientRoleException
├── UserException
│   ├── UserNotFoundException
│   ├── DuplicateUsernameException
│   ├── DuplicateEmailException
│   └── UserProfileUploadException
├── ChannelException
│   ├── ChannelNotFoundException
│   ├── DuplicateChannelException
│   ├── PrivateChannelUpdateException
│   └── UsersNotFoundException
├── MessageException
│   ├── MessageNotFoundException
│   ├── MessageEditForbiddenException
│   └── MessageDeleteForbiddenException
├── ReadStatusException
│   └── ReadStatusNotFoundException
└── BinaryContentException
    ├── BinaryContentNotFoundException
    ├── BinaryContentUploadException
    └── BinaryContentStorageException
```

**ErrorCode Enum:**

- Defines HTTP status codes and error messages for all business exceptions
- Located at: `com.sprint.mission.discodeit.exception.ErrorCode`
- Maps to DiscodeitException via the `errorCode` field

**Global Exception Handler:**

- Implemented using `@RestControllerAdvice` in `GlobalExceptionHandler` class
- Returns consistent `ErrorResponse` objects with status, exceptionType, message, and details

### Configuration and Profiles

**Profiles:**

- `local` (default): Development mode (DEBUG logging, localhost:5432 PostgreSQL, server on 8080)
- `prod`: Production mode (WARN/INFO logging, environment-based DB config, server on port 80)
- `test`: Test mode (H2 in-memory DB in PostgreSQL compatibility mode, Swagger disabled)

**Key Configuration Files:**

- `application.yaml`: Base configuration, defaults to `local` profile
- `application-local.yaml`: Local development settings (DEBUG logging, all Actuator endpoints exposed)
- `application-prod.yaml`: Production settings (restricted logging, limited health endpoint details)
- `application-test.yaml`: Test configuration with H2 database
- `logback-spring.xml`: Custom logging configuration with daily rolling logs

**Environment Variables:**

Database:
- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username (local default: `discodeit_user`)
- `SPRING_DATASOURCE_PASSWORD`: Database password (local default: `discodeit1234`)

Storage:
- `STORAGE_TYPE`: Storage type (`local` or `s3`, default: `local`)
- `STORAGE_LOCAL_ROOT_PATH`: Local storage path (default: `.discodeit/storage`)
- `AWS_S3_ACCESS_KEY`, `AWS_S3_SECRET_KEY`, `AWS_S3_REGION`, `AWS_S3_BUCKET`: S3 configuration

JWT Authentication:
- `JWT_ACCESS_SECRET`: Secret key for access token signing (required)
- `JWT_ACCESS_SECRET_PREVIOUS`: Previous secret for graceful rotation (optional)
- `JWT_ACCESS_EXPIRATION`: Access token expiration in ms (default: 1800000 = 30 min)
- `JWT_REFRESH_SECRET`: Secret key for refresh token signing (required)
- `JWT_REFRESH_SECRET_PREVIOUS`: Previous secret for graceful rotation (optional)
- `JWT_REFRESH_EXPIRATION`: Refresh token expiration in ms (default: 604800000 = 7 days)
- `JWT_MAX_SESSIONS`: Max concurrent sessions per user (default: 1)

Rate Limiting:
- `RATE_LIMIT_MAX_ATTEMPTS`: Max login attempts (default: 5)
- `RATE_LIMIT_WINDOW_SECONDS`: Time window for attempts (default: 60)
- `RATE_LIMIT_BLOCK_SECONDS`: Block duration after exceeding attempts (default: 300)

Admin User:
- `DISCODEIT_ADMIN_USERNAME`, `DISCODEIT_ADMIN_EMAIL`, `DISCODEIT_ADMIN_PASSWORD`: Admin user credentials

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
├── config/              # Test configuration (TestSecurityConfig)
├── controller/          # WebMvc slice tests (@WebMvcTest)
├── integration/         # Full integration tests (@SpringBootTest)
├── repository/          # Repository slice tests (@DataJpaTest)
├── scheduler/           # Scheduler tests
├── security/            # Security test utilities (WithMockDiscodeitUser)
├── service/             # Service unit tests
└── storage/             # Storage implementation tests (local, S3 with Testcontainers)
```

**Test Configuration:**

- H2 in-memory database in PostgreSQL mode
- `ddl-auto: create-drop` for clean state
- Test storage: `./build/.discodeit/storage`
- Swagger/OpenAPI disabled in test profile
- Custom security context factory for authenticated tests (`@WithMockDiscodeitUser`)

**Testing Strategy:**

- **Unit Tests**: Service layer with Mockito/BDDMockito for mocking repositories
- **Slice Tests**:
    - Repository tests with `@DataJpaTest`
    - Controller tests with `@WebMvcTest` and MockMvc
- **Integration Tests**: `@SpringBootTest` with full context and H2 database
- **S3 Tests**: Testcontainers with LocalStack for S3 integration testing
- Use `@Transactional` for test isolation
- Target: 60% code coverage (verified with JaCoCo)

**JaCoCo Coverage:**

- Excludes: config/, dto/, entity/, exception/, security/, storage/s3/, *Application.class
- Minimum requirement: 60% line coverage per class
- Run `./gradlew jacocoTestReport` to generate reports

### Monitoring and Actuator

**Exposed Endpoints:**

- `/actuator/health`: Application health status
- `/actuator/info`: Application metadata (name, version, Java version, config info)
- `/actuator/metrics`: Application metrics
- `/actuator/loggers`: Runtime logger configuration
- `/actuator/prometheus`: Prometheus metrics (prod profile only)

**Profile-specific Behavior:**

- `local`: All endpoints exposed (`*`), health details shown
- `prod`: Limited endpoints (health, info, metrics, loggers, prometheus), health details hidden
- `test`: All endpoints exposed, health details shown

### API Documentation

**OpenAPI/Swagger:**

- API docs: `http://localhost:8080/docs`
- Swagger UI: `http://localhost:8080/docs/ui`
- Configured via SpringDoc (`springdoc-openapi-starter-webmvc-ui`)
- Enabled in local/prod, disabled in test profile
- Controller documentation interfaces in `docs/` package (e.g., `UserControllerDocs`, `AuthControllerDocs`)

### Security

**Authentication:**

- JWT-based stateless authentication using Nimbus JOSE+JWT library
- Access tokens (default 30 min expiration) + Refresh tokens (default 7 days, stored in HttpOnly cookie)
- Form login at `/api/auth/login` with custom success/failure handlers
- Token refresh at `/api/auth/refresh`
- Graceful secret rotation support (current + previous secret validation)

**Authorization:**

- Role-based access control with hierarchy: ADMIN > CHANNEL_MANAGER > USER
- Method-level security via `@EnableMethodSecurity`
- Public endpoints: `/api/auth/csrf-token`, `/api/users` (POST), `/api/auth/login`, `/api/auth/logout`, `/api/auth/refresh`
- All `/api/**` endpoints require authentication

**Security Features:**

- CSRF protection with cookie-based token repository (SPA-compatible)
- Rate limiting on login attempts (configurable max attempts, window, block duration)
- In-memory JWT registry for session management (max sessions per user)
- Authentication audit logging via `AuthAuditService`

**Password Handling:**

- BCrypt password encoder (Spring default: 10 rounds)

**Key Security Components:**

- `JwtTokenProvider`: Token generation and validation
- `JwtAuthenticationFilter`: Extracts and validates JWT from Authorization header
- `JwtLoginSuccessHandler`: Issues tokens on successful login
- `JwtLogoutHandler`: Invalidates tokens on logout
- `LoginRateLimitFilter`: Prevents brute force attacks
- `RateLimiterService`: Tracks login attempts per IP/username

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
    - Exceptions are handled by `GlobalExceptionHandler` with consistent ErrorResponse format

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
    - Use `@WithMockDiscodeitUser` for authenticated controller tests
    - Ensure 60% minimum code coverage per class

5. **File Handling**:
    - Always use BinaryContentStorage abstraction
    - Store metadata in database via BinaryContentService
    - Clean up orphaned files via FileCleanupScheduler
    - Validate file size/type at controller layer

6. **Security**:
    - Public endpoints must be added to SecurityConfig's permitAll() list
    - Use `@PreAuthorize` for method-level authorization
    - Access current user via `@AuthenticationPrincipal DiscodeitUserDetails`
    - Add authentication audit logging for sensitive operations

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
