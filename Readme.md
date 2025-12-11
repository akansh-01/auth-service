# Auth Service (Hospital Management Project)

This is the **Authentication & Authorization Microservice** for the Hospital Management platform. It handles user registration, secure login, and JWT (JSON Web Token) generation/validation.

## 🚀 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x
* **Build Tool:** Gradle
* **Database:** PostgreSQL
* **Database Migration:** Flyway
* **Security:** Spring Security 6 + JWT (jjwt 0.11.5)

---

## 🛠️ Prerequisites

Before running the application, ensure you have the following installed:

1.  **Java 21 SDK**
2.  **PostgreSQL** (running on port `5432`)
3.  **Git**

---

## ⚙️ Setup & Configuration

### 1. Database Setup
Create a PostgreSQL database named `auth_project`. The application uses the `auth` schema, which Flyway will create automatically.

```sql
-- Run this in your Postgres CLI or pgAdmin
CREATE DATABASE auth_project;