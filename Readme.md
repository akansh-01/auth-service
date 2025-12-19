# Auth Service - Healthcare System by HungryCoders

This is the **Authentication & Authorization Microservice** for the Healthcare Management platform. It handles user registration, secure login, JWT (JSON Web Token) generation/validation, and integrates with the microservices ecosystem via Spring Cloud.

## 🏗️ Architecture Overview

Based on the Healthcare System architecture diagram, this service:
- Registers with **Eureka Service Discovery** for dynamic service registration
- Provides **JWT-based authentication** with role-based access (DOCTOR, PATIENT, ADMIN)
- Exposes **health check endpoints** monitored by Spring Boot Admin Server
- Supports **distributed tracing** via Zipkin/Micrometer
- Integrates with **API Gateway** for centralized authentication
- **Dockerized** for container orchestration

---

## 🚀 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x
* **Build Tool:** Gradle
* **Database:** PostgreSQL 16
* **Database Migration:** Flyway
* **Security:** Spring Security 6 + JWT (jjwt 0.11.5)
* **Service Discovery:** Netflix Eureka Client
* **Monitoring:** Spring Boot Actuator
* **Tracing:** Micrometer + Zipkin
* **Containerization:** Docker & Docker Compose

---

## 🛠️ Prerequisites

### For Local Development:
1. **Java 21 SDK**
2. **PostgreSQL** (running on port `5432`)
3. **Git**
4. **Gradle** (or use included Gradle wrapper)

### For Docker Deployment:
1. **Docker** (version 20.x or higher)
2. **Docker Compose** (version 2.x or higher)

---

## ⚙️ Setup & Configuration

### Option 1: Local Development Setup

#### 1. Database Setup
Create a PostgreSQL database named `auth_project`:

```sql
-- Run this in your Postgres CLI or pgAdmin
CREATE DATABASE auth_project;
```

#### 2. Configuration
The application uses `application.yml` for local development. Update credentials if needed:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_project
    username: postgres  # Update if needed
    password: root      # Update if needed
```

#### 3. Run the Application

```bash
# Using Gradle wrapper
./gradlew bootRun

# Or build and run
./gradlew clean build
java -jar build/libs/auth-0.0.1-SNAPSHOT.jar
```

The service will start on **port 9898**.

---

### Option 2: Docker Deployment (Recommended)

The `docker-compose.yml` includes the complete Healthcare System infrastructure:
- PostgreSQL Database
- Eureka Server (Service Discovery)
- Spring Boot Admin Server
- Zipkin (Distributed Tracing)
- Kafka & Zookeeper (Event Streaming)
- Auth Service

#### Start All Services:

```bash
# Start all infrastructure services
docker-compose up -d

# View logs
docker-compose logs -f auth-service

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

#### Build Only Auth Service:

```bash
# Build the Docker image
docker build -t hungrycoders/auth-service:latest .

# Run standalone
docker run -p 9898:9898 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/auth_project \
  hungrycoders/auth-service:latest
```

---

## 📡 API Endpoints

### Authentication Endpoints

#### 1. Register User
```http
POST http://localhost:9898/auth/register
Content-Type: application/json

{
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "password": "SecurePassword123!",
  "role": "PATIENT",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

**Response:** Returns the user UUID
```json
"f47ac10b-58cc-4372-a567-0e02b2c3d479"
```

#### 2. Generate JWT Token (Login)
```http
POST http://localhost:9898/auth/token
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

**Response:** Returns JWT token (valid for 24 hours)
```json
"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJmNDdhYzEwYi01OGNjLTQzNzItYTU2Ny0wZTAyYjJjM2Q0NzkiLCJyb2xlIjoiUEFUSUVOVCIsInN1YiI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwiaWF0IjoxNjg5MDAwMDAwLCJleHAiOjE2ODkwODY0MDB9.signature"
```

#### 3. Validate Token
```http
GET http://localhost:9898/auth/validate?token=YOUR_JWT_TOKEN
```

**Response:**
```json
"Token is valid"
```

#### 4. Extract User Info from Token (For API Gateway)
```http
GET http://localhost:9898/auth/user-info?token=YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "email": "john.doe@example.com",
  "role": "PATIENT",
  "isValid": true
}
```

---

### Health Check & Monitoring Endpoints

```http
# Health check
GET http://localhost:9898/actuator/health

# Application info
GET http://localhost:9898/actuator/info

# Metrics
GET http://localhost:9898/actuator/metrics

# Prometheus metrics
GET http://localhost:9898/actuator/prometheus
```

---

## 🔐 Security Features

### JWT Token Structure
The JWT token includes:
- **Subject:** User email
- **Claims:**
  - `userId`: Unique user identifier (UUID)
  - `role`: User role (DOCTOR, PATIENT, ADMIN)
  - `iat`: Issued at timestamp
  - `exp`: Expiration timestamp (24 hours from issuance)

### User Roles
- **PATIENT**: Book appointments, view own medical records
- **DOCTOR**: View appointments, manage patient records
- **ADMIN**: Full system access, user management

---

## 🏥 Integration with Healthcare System

### Service Discovery
The Auth Service registers with Eureka Server at startup:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### API Gateway Integration
The API Gateway should:
1. Call `/auth/user-info?token=XXX` to validate and extract user details
2. Forward requests to downstream services with user context
3. Implement circuit breaker patterns for fault tolerance

### Spring Boot Admin
The Auth Service registers with Spring Boot Admin Server for:
- Real-time health monitoring
- Application metrics
- Log viewing
- Environment properties

---

## 🐳 Docker Configuration

### Service Ports
- **Auth Service**: 9898
- **PostgreSQL**: 5432
- **Eureka Server**: 8761
- **Spring Boot Admin**: 9090
- **Zipkin**: 9411
- **Kafka**: 9092

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://auth-db:5432/auth_project
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=root

# Eureka
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# Zipkin
MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://zipkin:9411/api/v2/spans
```

---

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

### Test User Registration
```bash
curl -X POST http://localhost:9898/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Test",
    "lastname": "User",
    "email": "test@example.com",
    "phone": "+1234567890",
    "password": "Test123!",
    "role": "PATIENT"
  }'
```

### Test Login
```bash
curl -X POST http://localhost:9898/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

---

## 📊 Monitoring & Observability

### Access Points
- **Eureka Dashboard**: http://localhost:8761
- **Spring Boot Admin**: http://localhost:9090
- **Zipkin Tracing**: http://localhost:9411
- **Health Check**: http://localhost:9898/actuator/health

### Logs
```bash
# Docker logs
docker-compose logs -f auth-service

# Local logs
tail -f logs/spring-boot-logger.log
```

---

## 🔄 Next Steps for Complete Healthcare System

Based on the architecture diagram, you'll need to create these additional services:

1. **API Gateway** (Spring Cloud Gateway)
   - Route requests to microservices
   - JWT validation via Auth Service
   - Circuit breaker with Resilience4j
   - Rate limiting

2. **Doctor Service**
   - Doctor registration and profile management
   - Availability management
   - Specialization handling

3. **Patient Service**
   - Patient registration and profile management
   - Medical history
   - Prescription management

4. **Book Appointment Service**
   - Appointment scheduling
   - Doctor-Patient matching
   - Kafka event publishing

5. **Notification Service**
   - Email notifications
   - SMS notifications
   - Kafka event consumption

6. **Eureka Server**
   - Service discovery
   - Load balancing

7. **Spring Boot Admin Server**
   - Centralized monitoring
   - Health checks aggregation

---

## 📝 Database Schema

The Auth Service uses the `auth` schema with the following tables:

### user_details
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| firstname | VARCHAR | User's first name |
| lastname | VARCHAR | User's last name |
| fullname | VARCHAR | Generated full name |
| email | VARCHAR | Unique email (login) |
| phone | VARCHAR | Contact number |
| password | VARCHAR | Bcrypt hashed password |
| role | VARCHAR | User role (DOCTOR/PATIENT/ADMIN) |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Last update time |

### address
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| user_id | UUID | Foreign key to user_details |
| street | VARCHAR | Street address |
| city | VARCHAR | City |
| state | VARCHAR | State/Province |
| zip_code | VARCHAR | Postal code |
| country | VARCHAR | Country |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Submit a Pull Request

---

## 📄 License

This project is part of the Healthcare System by HungryCoders.

---

## 📧 Contact

For questions or support, please contact the HungryCoders team.

---

## 🎯 Key Changes Made for Microservices Architecture

### ✅ Added Spring Cloud Dependencies
- Netflix Eureka Client for service discovery
- Spring Cloud Config Client for centralized configuration
- Spring Boot Actuator for health checks and monitoring
- Micrometer Tracing with Zipkin for distributed tracing

### ✅ Enhanced JWT Service
- Added `userId` and `role` to JWT claims
- Increased token expiration to 24 hours (was 1 minute)
- Added methods to extract user info from tokens
- Added token validation with username

### ✅ New Endpoints
- `/auth/user-info` - Extract user details from token (for API Gateway)
- Actuator endpoints for health checks and metrics

### ✅ Docker Support
- Multi-stage Dockerfile for optimized builds
- Docker Compose with full infrastructure (PostgreSQL, Eureka, Kafka, Zipkin, etc.)
- Separate `application-docker.yml` for container environment

### ✅ Configuration Updates
- Service name changed to `auth-service`
- Configured Eureka client registration
- Added actuator endpoints exposure
- Added distributed tracing configuration
- Port set to 9898

### ✅ Monitoring & Observability
- Health check endpoints enabled
- Application info exposed
- Prometheus metrics enabled
- Distributed tracing with Zipkin

---

**Ready to build the complete Healthcare System! 🏥**
