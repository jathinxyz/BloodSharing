# Blood Matcher — Backend API Service

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-red.svg)](https://flywaydb.org/)
[![Swagger OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI%203-blue.svg)](http://localhost:8080/swagger-ui.html)
[![Build Status](https://img.shields.io/badge/Tests-50%2F50%20Passing-success.svg)](#running-automated-tests)

Backend service for **Blood Matcher — Smart Blood Donor and Recipient Matching System**, built with Spring Boot 3, Java 21, Spring Security with stateless JWT, Spring Data JPA, and Flyway database migrations.

---

## 1. Important Medical & Legal Notice

> **IMPORTANT DISCLAIMER:**
> Blood Matcher's blood compatibility and matching engine performs **preliminary software screening** based solely on standard ABO/Rh red-blood-cell compatibility rules and geographical proximity.
>
> **It is NOT a medical clearance system and does not guarantee transfusion safety.** Formal clinical blood bank typing, antibody screening, infectious disease testing, laboratory cross-matching, and medical eligibility assessments by licensed medical personnel remain mandatory before any blood donation or transfusion.

---

## 2. Technology Stack & Prerequisites

- **Java**: Java 21 LTS (verified with OpenJDK / Oracle JDK 21).
- **Build Tool**: Apache Maven 3.9+ (or included `./mvnw` / `mvnw.cmd`).
- **Framework**: Spring Boot 3.3.4.
- **Persistence**: Spring Data JPA & Hibernate 6.5.
- **Security**: Spring Security 6 with JJWT 0.12.6 (stateless HMAC-SHA JWT) and BCrypt password hashing.
- **Migrations**: Flyway Migration Tool (`V1__init_schema.sql`).
- **Database**:
  - Development / Production: MySQL 8.0+ / Aiven MySQL with TLS.
  - Test Suite: In-memory H2 (in MySQL mode with Flyway schema migration).
- **Validation**: Jakarta Bean Validation (Hibernate Validator).
- **Documentation**: SpringDoc OpenAPI 2.6.0 (Swagger UI at `/swagger-ui.html`).
- **Actuator**: Health and telemetry (`/actuator/health`).

---

## 3. Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/bloodmatcher/
│   │   │   ├── BloodMatcherApplication.java    # Spring Boot entry point
│   │   │   ├── admin/                         # Admin dashboard, stats, oversight
│   │   │   ├── auth/                          # Authentication, JWT, Refresh tokens
│   │   │   ├── common/                        # Enums, ApiResponse, PagedResponse, Exceptions
│   │   │   ├── config/                        # SecurityConfig, CorsConfig, OpenApiConfig, DataInitializer
│   │   │   ├── donation/                      # Donation records and donation history
│   │   │   ├── donor/                         # Donor profiles, coordinates, availability
│   │   │   ├── invitation/                    # Invitations, responses (accept/reject)
│   │   │   ├── matching/                      # Compatibility matrix, Haversine distance, Matching engine
│   │   │   ├── notification/                  # In-app notifications & safe SMTP email delivery
│   │   │   ├── publicinfo/                    # Public health disclaimers & compatibility reference
│   │   │   ├── request/                       # Blood requests lifecycle & fulfillment
│   │   │   ├── security/                      # UserPrincipal, CustomUserDetailsService, JwtTokenProvider, JwtAuthFilter
│   │   │   └── user/                          # User entity, UserService, UserDto
│   │   └── resources/
│   │       ├── application.yml                # Main application configuration
│   │       └── db/migration/
│   │           └── V1__init_schema.sql        # Flyway schema migration
│   └── test/
│       ├── java/com/bloodmatcher/             # 50 unit and integration tests
│       └── resources/
│           └── application.yml                # H2 test database profile
├── Dockerfile                                 # Multi-stage container build
├── .env.example                               # Environment variable reference template
├── .gitignore                                 # Git ignore rules
├── mvnw & mvnw.cmd                            # Maven wrapper scripts
└── pom.xml                                    # Maven project definition
```

---

## 4. Environment Variables Reference

| Variable | Description | Default (Local Dev) |
|---|---|---|
| `PORT` | HTTP port the application binds to | `8080` |
| `DB_HOST` | MySQL hostname | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database schema name | `bloodmatcher` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | *(empty)* |
| `DB_SSL_MODE` | MySQL SSL Mode (`PREFERRED`, `REQUIRED`, `DISABLED`) | `PREFERRED` |
| `SPRING_DATASOURCE_URL` | Optional full JDBC URL override (e.g. for Aiven) | Derived from `DB_*` |
| `JWT_SECRET` | HMAC-SHA256 secret key (minimum 32 characters) | Built-in dev secret |
| `JWT_EXPIRATION_MS` | Access token lifespan in milliseconds | `86400000` (24 hours) |
| `JWT_REFRESH_EXPIRATION_MS`| Refresh token lifespan in milliseconds | `604800000` (7 days) |
| `APP_CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | `http://localhost:3000,http://localhost:5173` |
| `ADMIN_EMAIL` | Initial admin account email (bootstrapped once) | `admin@bloodmatcher.org` |
| `ADMIN_PASSWORD` | Initial admin password (bootstrapped once) | `Admin@BloodMatcher2024!` |
| `ADMIN_NAME` | Initial admin full name | `System Administrator` |
| `MAIL_HOST` | Optional SMTP server host (e.g. `smtp.gmail.com`) | *(blank = email disabled)* |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | SMTP username | *(blank)* |
| `MAIL_PASSWORD` | SMTP password / app password | *(blank)* |
| `MAIL_FROM` | Sender address | `noreply@bloodmatcher.org` |

---

## 5. Local Setup & Running

### Prerequisites
1. Ensure Java 21 is installed:
   ```bash
   java -version
   ```
2. Start a local MySQL database or run one with Docker:
   ```bash
   docker run --name bloodmatcher-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=bloodmatcher -p 3306:3306 -d mysql:8.0
   ```

### Running with Maven Wrapper
- **Windows**:
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```

Once running:
- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

---

## 6. Running Automated Tests

The automated test suite runs completely independently using an in-memory H2 database (in MySQL mode with Flyway schema migration validation). **No external database or internet connection is required to run the tests.**

```bash
# Windows
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

### Test Coverage Highlights (50 Automated Tests)
- **Blood Compatibility**: Every combination of recipient and donor in the ABO/Rh matrix (O-, O+, A-, A+, B-, B+, AB-, AB+) verified, including invalid string parsing and illegal argument checks.
- **Distance Calculations**: Haversine formula verified against known geographic coordinates (e.g. London to Paris ~343 km), zero distance, missing coordinates (returns `null`), and out-of-range bounds checks (-90 to +90, -180 to +180).
- **Authentication & Security**: Email normalization, duplicate email rejection (409 Conflict), public ADMIN registration rejection (400 Bad Request), password hashing with BCrypt, JWT generation & verification, refresh token rotation.
- **Request Lifecycle**: Creation (OPEN), cancellation by owner, rejection of unauthorized cancellation (403 Forbidden), rejection of terminal request modifications (409 Conflict), and explicit fulfillment workflow.
- **Matching Engine**: Excludes requester's own profile, excludes unavailable donors, excludes donors on donation cooldown (< 56 days), prioritizes closer donors over distant donors, and handles missing coordinates without crashing.
- **Invitations**: Prevents duplicate invitations, donor acceptance updates request to `IN_PROGRESS`, donor declination, rejection of unauthorized responses, and conditional privacy-respecting contact disclosure.

---

## 7. REST API Documentation & Examples

All response bodies follow a consistent structure:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-10-15T07:00:00Z"
}
```

Error responses follow RFC 7807 principles:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Public registration cannot assign the ADMIN role",
  "path": "/api/auth/register",
  "timestamp": "2026-10-15T07:00:00Z"
}
```

### 1. Authentication Endpoints

#### Register as Donor
- **Endpoint**: `POST /api/auth/register`
- **Public access**: Yes
- **Request**:
  ```json
  {
    "fullName": "Jane Doe",
    "email": "jane.doe@example.com",
    "password": "SecurePassword123!",
    "role": "DONOR",
    "bloodGroup": "O+",
    "phone": "+1-555-0199",
    "address": "123 Elm St, Springfield",
    "latitude": 39.7817,
    "longitude": -89.6501
  }
  ```
- **Response** (`201 Created`):
  ```json
  {
    "success": true,
    "message": "Registration successful",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "expiresInMs": 86400000,
      "refreshToken": "48f93a20-3375-47e9-a29f-38a5d3f23a8b",
      "user": {
        "id": 2,
        "fullName": "Jane Doe",
        "email": "jane.doe@example.com",
        "role": "DONOR",
        "enabled": true
      }
    }
  }
  ```

#### Login
- **Endpoint**: `POST /api/auth/login`
- **Request**:
  ```json
  {
    "email": "jane.doe@example.com",
    "password": "SecurePassword123!"
  }
  ```

#### Refresh Token
- **Endpoint**: `POST /api/auth/refresh`
- **Request**:
  ```json
  {
    "refreshToken": "48f93a20-3375-47e9-a29f-38a5d3f23a8b"
  }
  ```

#### Logout
- **Endpoint**: `POST /api/auth/logout`
- **Headers**: `Authorization: Bearer <token>`

---

### 2. Blood Request Endpoints

#### Create Request (Recipient)
- **Endpoint**: `POST /api/requests`
- **Headers**: `Authorization: Bearer <token>`
- **Request**:
  ```json
  {
    "bloodGroup": "A+",
    "unitsRequested": 2,
    "hospitalName": "Memorial Hospital",
    "hospitalLocation": "456 Oak Avenue, Ward 4",
    "latitude": 39.7900,
    "longitude": -89.6400,
    "urgency": "HIGH",
    "requiredDate": "2026-10-18",
    "notes": "Emergency surgery"
  }
  ```

#### Preview Matches for Request
- **Endpoint**: `GET /api/requests/{requestId}/matches?limit=10`
- **Headers**: `Authorization: Bearer <token>` (Owner or Admin)
- **Response**:
  ```json
  {
    "success": true,
    "data": [
      {
        "donorId": 1,
        "donorName": "Jane Doe",
        "bloodGroup": "O+",
        "generalLocation": "123 Elm St, Springfield",
        "distanceKm": 1.25,
        "distanceDescription": "1.25 km away",
        "compatibilityOutcome": "Donor O+ is compatible with recipient A+ for preliminary red-blood-cell screening.",
        "eligible": true,
        "cooldownEligible": true,
        "cooldownDaysRemaining": 0,
        "alreadyInvited": false
      }
    ]
  }
  ```

#### Trigger Matching Engine & Send Invitations
- **Endpoint**: `POST /api/requests/{requestId}/match`
- **Headers**: `Authorization: Bearer <token>` (Owner or Admin)
- **Request**:
  ```json
  {
    "limit": 5
  }
  ```

#### Cancel Request
- **Endpoint**: `POST /api/requests/{requestId}/cancel`
- **Headers**: `Authorization: Bearer <token>` (Owner)

#### Fulfill Request
- **Endpoint**: `POST /api/requests/{requestId}/fulfill`
- **Headers**: `Authorization: Bearer <token>` (Owner or Admin)
- **Request**:
  ```json
  {
    "donorId": 1,
    "notes": "Donation completed at Springfield Blood Bank"
  }
  ```

---

### 3. Donor Operations

- `GET /api/donors/me` — Retrieve donor profile.
- `PUT /api/donors/me` — Update donor profile (contact info, address, coordinates).
- `PATCH /api/donors/me/availability` — Toggle donor availability (`{"available": false}`).
- `GET /api/donors/me/invitations` — List invitations addressed to the donor.
- `GET /api/donors/me/donations` — List recorded donations for the donor.
- `POST /api/donors/me/invitations/{invitationId}/accept` — Accept invitation (shares contact phone with requester).
- `POST /api/donors/me/invitations/{invitationId}/reject` — Decline invitation.

---

### 4. Admin Endpoints (`ROLE_ADMIN`)

- `GET /api/admin/dashboard` — Real operational metrics: user counts, donor availability, request statuses, donation counts, and invitation metrics.
- `GET /api/admin/users?role=DONOR&page=0&size=20` — Paginated user directory.
- `PATCH /api/admin/users/{userId}/status` — Enable or disable accounts (`{"enabled": false}`).
- `GET /api/admin/requests?status=OPEN` — System-wide blood request management.
- `GET /api/admin/reports` — Blood group supply vs. demand distributions and fulfillment rates.

---

### 5. Public Endpoints (No Authentication Required)

- `GET /api/public/info` — System mission and operational parameters.
- `GET /api/public/disclaimers` — Health screening legal notice.
- `GET /api/public/compatibility-matrix` — Standard ABO/Rh RBC compatibility chart.
- `GET /actuator/health` — Spring Boot Actuator health status.

---

## 8. Deployment Guides

### Aiven MySQL Configuration (TLS / SSL)
When connecting to an Aiven MySQL service:
1. Obtain the host, port, database, username, and password from your Aiven Console.
2. In Aiven MySQL, SSL is mandatory. Set `DB_SSL_MODE=REQUIRED`.
3. If using Aiven's Project CA certificate:
   - Download the CA certificate (`ca.pem`) from the Aiven Console.
   - You can supply it to Java via truststore or pass the Aiven JDBC URL with `sslMode=REQUIRED`.
   - In `application.yml`, set:
     ```bash
     SPRING_DATASOURCE_URL=jdbc:mysql://<aiven-host>:<aiven-port>/defaultdb?sslMode=REQUIRED
     DB_USERNAME=<aiven-user>
     DB_PASSWORD=<aiven-password>
     ```
4. **Never disable TLS certificate verification in production.**

### Railway Deployment
The backend includes a verified multi-stage `Dockerfile`:
1. Connect your repository to Railway and select the `backend` directory as the root.
2. Set environment variables in the Railway dashboard:
   - `PORT`: Automatically set by Railway.
   - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.
   - `JWT_SECRET`: A secure random string with at least 32 characters.
   - `APP_CORS_ALLOWED_ORIGINS`: Your frontend deployment URL (e.g. `https://my-app.vercel.app`).
   - `ADMIN_EMAIL` & `ADMIN_PASSWORD`: For initial administrator access.
3. Railway will build the container with Temurin 21 and start the service with non-root privileges.

---

## 9. Security & Privacy Guarantees

1. **Role Protection**: Public registration rejects `ROLE_ADMIN` assignment. The first administrator is provisioned once via database initialization (`DataInitializer`) using environment variables.
2. **Password Security**: Passwords are never stored in plaintext and are hashed using BCrypt.
3. **Data Masking**: Password hashes and internal security fields are never exposed via DTOs or JSON responses.
4. **Donor Privacy**: Donor contact telephone numbers and exact coordinates are strictly concealed from recipients until an invitation is explicitly accepted by the donor.
5. **Ownership Enforcement**: Service-layer authorization prevents recipients from modifying others' requests and donors from responding to invitations not addressed to them.
6. **Graceful Error Handling**: Stack traces and SQL queries are never leaked to API clients. Centralized exception handling maps errors to RFC 7807 JSON responses.
