# Auth Service API Examples

This document provides curl examples and HTTP requests for testing the Auth Service API.

## Base URL
- **Local:** `http://localhost:9898`
- **Docker:** `http://localhost:9898`

---

## 1. Register a New User

### As a Patient
```bash
curl -X POST http://localhost:9898/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@hospital.com",
    "phone": "+1-555-0123",
    "password": "SecurePassword123!",
    "role": "PATIENT",
    "address": {
      "street": "123 Main Street",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
  }'
```

### As a Doctor
```bash
curl -X POST http://localhost:9898/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Sarah",
    "lastname": "Smith",
    "email": "dr.smith@hospital.com",
    "phone": "+1-555-0456",
    "password": "DoctorPass123!",
    "role": "DOCTOR",
    "address": {
      "street": "456 Medical Plaza",
      "city": "Boston",
      "state": "MA",
      "zipCode": "02101",
      "country": "USA"
    }
  }'
```

### As an Admin
```bash
curl -X POST http://localhost:9898/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Admin",
    "lastname": "User",
    "email": "admin@hospital.com",
    "phone": "+1-555-0789",
    "password": "AdminPass123!",
    "role": "ADMIN",
    "address": {
      "street": "789 Hospital Rd",
      "city": "Chicago",
      "state": "IL",
      "zipCode": "60601",
      "country": "USA"
    }
  }'
```

**Expected Response:**
```
"f47ac10b-58cc-4372-a567-0e02b2c3d479"
```
(Returns the generated UUID for the user)

---

## 2. Login / Generate JWT Token

```bash
curl -X POST http://localhost:9898/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@hospital.com",
    "password": "SecurePassword123!"
  }'
```

**Expected Response:**
```
"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJmNDdhYzEwYi01OGNjLTQzNzItYTU2Ny0wZTAyYjJjM2Q0NzkiLCJyb2xlIjoiUEFUSUVOVCIsInN1YiI6ImpvaG4uZG9lQGhvc3BpdGFsLmNvbSIsImlhdCI6MTcwMzYwMDAwMCwiZXhwIjoxNzAzNjg2NDAwfQ.signature_here"
```

**Save this token for subsequent requests!**

---

## 3. Validate Token

```bash
# Replace YOUR_JWT_TOKEN with the actual token from login
curl -X GET "http://localhost:9898/auth/validate?token=YOUR_JWT_TOKEN"
```

**Expected Response:**
```
"Token is valid"
```

**Error Response (if expired/invalid):**
```json
{
  "timestamp": "2024-12-19T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token is expired or invalid"
}
```

---

## 4. Extract User Info from Token

This endpoint is primarily used by the API Gateway to extract user details from the JWT token.

```bash
# Replace YOUR_JWT_TOKEN with the actual token
curl -X GET "http://localhost:9898/auth/user-info?token=YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "email": "john.doe@hospital.com",
  "role": "PATIENT",
  "isValid": true
}
```

**Error Response (if token is invalid):**
```json
{
  "userId": null,
  "email": null,
  "role": null,
  "isValid": false
}
```

---

## 5. Health Check (Actuator)

```bash
curl -X GET http://localhost:9898/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760,
        "path": "/app",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

## 6. Application Info

```bash
curl -X GET http://localhost:9898/actuator/info
```

**Expected Response:**
```json
{
  "app": {
    "name": "Auth Service",
    "description": "Authentication and Authorization Service for Healthcare System",
    "version": "1.0.0"
  }
}
```

---

## 7. Prometheus Metrics

```bash
curl -X GET http://localhost:9898/actuator/prometheus
```

**Expected Response:**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Eden Space",} 1.234567E7
...
```

---

## Complete Workflow Example

### Step 1: Register a Patient
```bash
TOKEN=$(curl -s -X POST http://localhost:9898/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Jane",
    "lastname": "Patient",
    "email": "jane.patient@email.com",
    "phone": "+1-555-1111",
    "password": "Patient123!",
    "role": "PATIENT"
  }')

echo "User ID: $TOKEN"
```

### Step 2: Login and Get JWT
```bash
JWT_TOKEN=$(curl -s -X POST http://localhost:9898/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.patient@email.com",
    "password": "Patient123!"
  }')

echo "JWT Token: $JWT_TOKEN"
```

### Step 3: Validate the Token
```bash
curl -X GET "http://localhost:9898/auth/validate?token=$JWT_TOKEN"
```

### Step 4: Extract User Info
```bash
curl -s -X GET "http://localhost:9898/auth/user-info?token=$JWT_TOKEN" | jq '.'
```

---

## PowerShell Examples (Windows)

### Register User
```powershell
$body = @{
    firstname = "John"
    lastname = "Doe"
    email = "john.doe@hospital.com"
    phone = "+1-555-0123"
    password = "SecurePassword123!"
    role = "PATIENT"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9898/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Generate Token
```powershell
$loginBody = @{
    email = "john.doe@hospital.com"
    password = "SecurePassword123!"
} | ConvertTo-Json

$token = Invoke-RestMethod -Uri "http://localhost:9898/auth/token" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginBody

Write-Host "Token: $token"
```

### Validate Token
```powershell
Invoke-RestMethod -Uri "http://localhost:9898/auth/validate?token=$token" -Method Get
```

---

## Postman Collection

You can import the following JSON into Postman:

```json
{
  "info": {
    "name": "Healthcare Auth Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"firstname\": \"John\",\n  \"lastname\": \"Doe\",\n  \"email\": \"john.doe@hospital.com\",\n  \"phone\": \"+1-555-0123\",\n  \"password\": \"SecurePassword123!\",\n  \"role\": \"PATIENT\"\n}"
        },
        "url": {
          "raw": "http://localhost:9898/auth/register",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9898",
          "path": ["auth", "register"]
        }
      }
    },
    {
      "name": "Generate Token",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"john.doe@hospital.com\",\n  \"password\": \"SecurePassword123!\"\n}"
        },
        "url": {
          "raw": "http://localhost:9898/auth/token",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9898",
          "path": ["auth", "token"]
        }
      }
    },
    {
      "name": "Validate Token",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:9898/auth/validate?token={{jwt_token}}",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9898",
          "path": ["auth", "validate"],
          "query": [
            {
              "key": "token",
              "value": "{{jwt_token}}"
            }
          ]
        }
      }
    },
    {
      "name": "Get User Info",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:9898/auth/user-info?token={{jwt_token}}",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9898",
          "path": ["auth", "user-info"],
          "query": [
            {
              "key": "token",
              "value": "{{jwt_token}}"
            }
          ]
        }
      }
    },
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:9898/actuator/health",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9898",
          "path": ["actuator", "health"]
        }
      }
    }
  ]
}
```

---

## Testing with HTTPie

If you have HTTPie installed:

```bash
# Register
http POST localhost:9898/auth/register \
  firstname=John lastname=Doe \
  email=john.doe@hospital.com \
  phone=+1-555-0123 \
  password=SecurePassword123! \
  role=PATIENT

# Login
http POST localhost:9898/auth/token \
  email=john.doe@hospital.com \
  password=SecurePassword123!

# Validate
http GET "localhost:9898/auth/validate?token=YOUR_TOKEN"
```

---

## Error Responses

### Invalid Credentials
```json
{
  "timestamp": "2024-12-19T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid access"
}
```

### User Already Exists
```json
{
  "timestamp": "2024-12-19T10:30:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "User with email already exists"
}
```

### Validation Error
```json
{
  "timestamp": "2024-12-19T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "must be a valid email",
    "password": "must be at least 8 characters"
  }
}
```

---

## Notes

1. **JWT Token Lifetime**: Tokens are valid for 24 hours
2. **Security**: Always use HTTPS in production
3. **Rate Limiting**: Consider implementing rate limiting for production
4. **Token Storage**: Never store tokens in localStorage in production; use httpOnly cookies
5. **Password Requirements**: Enforce strong password policies

---

**Happy Testing! 🚀**

