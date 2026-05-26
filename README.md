# Appointment Booking System

A Spring Boot REST API for appointment booking with JWT authentication, role-based access control, PostgreSQL, Docker, Swagger/OpenAPI documentation, and automated tests.

## Features

- User registration and login
- JWT-based authentication
- Role-based authorization
- Admin service management
- Admin specialist management
- Specialist availability slots
- Appointment booking
- Appointment cancellation
- Double-booking prevention
- Swagger/OpenAPI documentation
- PostgreSQL database
- Docker Compose setup
- Unit and integration tests

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Docker
- Swagger/OpenAPI
- JUnit 5
- Mockito
- Testcontainers

## Roles

| Role | Permissions |
|---|---|
| CLIENT | Book appointments, view own appointments, cancel own appointments |
| SPECIALIST | View own appointments |
| ADMIN | Manage services, specialists, availability slots |

## Main API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register client |
| POST | `/api/auth/login` | Login user |

### Services

| Method | Endpoint | Role |
|---|---|---|
| GET | `/api/services` | Public |
| GET | `/api/services/{id}` | Public |
| POST | `/api/admin/services` | ADMIN |

### Specialists

| Method | Endpoint | Role |
|---|---|---|
| GET | `/api/specialists` | Public |
| GET | `/api/specialists/{id}` | Public |
| POST | `/api/admin/specialists` | ADMIN |

### Availability

| Method | Endpoint | Role |
|---|---|---|
| GET | `/api/availability/specialists/{specialistId}` | Public |
| GET | `/api/admin/availability/specialists/{specialistId}` | ADMIN |
| POST | `/api/admin/availability` | ADMIN |

### Appointments

| Method | Endpoint | Role |
|---|---|---|
| POST | `/api/appointments` | CLIENT |
| GET | `/api/appointments/my` | CLIENT |
| GET | `/api/specialist/appointments` | SPECIALIST |
| PATCH | `/api/appointments/{appointmentId}/cancel` | CLIENT, SPECIALIST, ADMIN |

## How to Run Locally

### 1. Start PostgreSQL

```bash
docker compose up -d postgres