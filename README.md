# Utility Billing Platform

A configurable multi-tenant utility billing and ETL platform built with Spring Boot, React, and PostgreSQL.

## Features

- **6 Allocation Strategies** — Consumption, Occupancy, Headcount, Area, Bedroom, Fixed Percentage
- **Metadata-driven CSV ETL** — Upload meter readings with validation and audit trail
- **JWT Authentication + RBAC** — Admin, Owner, and Tenant roles
- **Full Audit Trail** — Every billing calculation stored with complete input/output snapshot
- **Idempotent ETL** — CSV imports are safe to re-run without duplicate data
- **Prometheus + Grafana** — Real-time observability out of the box

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4.1.0, Spring Security, Spring Batch |
| Database | PostgreSQL 15, Flyway (9 migrations) |
| Frontend | React 18, Vite, Tailwind CSS, Axios |
| Auth | JWT (HS256), BCrypt |
| Monitoring | Prometheus, Grafana |
| DevOps | Docker, Docker Compose, GitHub Actions |

## Quick Start (Local Development)

### Prerequisites
- Java 17
- Node.js 20
- Docker Desktop

### Run the full stack

```bash
docker compose up -d
```

This starts PostgreSQL, backend (port 8080), frontend (port 3000), Prometheus (port 9090), and Grafana (port 3001).

### Run individually (for development)

```bash
# Start database only
docker compose up -d postgres

# Start backend (IntelliJ or terminal)
cd backend && ./mvnw spring-boot:run

# Start frontend
cd frontend && npm run dev
```

## Default Credentials

| User | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |

## API Documentation

Base URL: `http://localhost:8080/api`

All endpoints except `/api/auth/login` require a Bearer token in the Authorization header.

### Authentication
- `POST /api/auth/login` — Returns JWT token

### Buildings
- `POST /api/buildings` — Create building
- `GET /api/buildings` — List all buildings
- `GET /api/buildings/{id}` — Get building

### Billing
- `POST /api/provider-bills` — Record utility provider bill
- `POST /api/provider-bills/{id}/generate` — Generate unit bills using allocation engine

### Payments
- `POST /api/unit-bills/{id}/payments` — Record payment
- `GET /api/unit-bills/{id}/payments` — Get payment history

### Import
- `POST /api/import/batches` — Upload CSV file
- `GET /api/import/batches/{id}` — Check import status
- `GET /api/import/batches/{id}/errors` — View row errors

## Monitoring

- Prometheus metrics: `http://localhost:9090`
- Grafana dashboards: `http://localhost:3001` (admin/admin)

## Architecture

```
React SPA → Spring Boot REST API → PostgreSQL
                ↓
          Allocation Engine (6 strategies)
          Spring Batch ETL Pipeline
          JWT Security Filter Chain
                ↓
          Prometheus → Grafana
```

## Git Workflow

- `main` — production-ready code
- Feature branches for new work
- Conventional commits: `feat:`, `fix:`, `chore:`, `docs:`