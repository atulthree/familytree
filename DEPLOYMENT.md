# Deployment Guide (Local and Production)

This document explains how to run the Family Tree application in local development and how to deploy it to a production environment.

## 1) Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ and npm
- PostgreSQL 14+ (or Docker for local database)
- Git

---

## 2) Local Deployment (Development)

### Step 1: Start PostgreSQL

Option A (recommended): use Docker Compose from the project root.

```bash
docker compose up -d postgres
```

Option B: use a locally installed PostgreSQL server and create a database named `familytree`.

### Step 2: Configure backend database settings

The backend reads database settings from:

- `backend/src/main/resources/application.yml`

For local docker-compose defaults, keep:

- host: `localhost`
- port: `5432`
- database: `familytree`
- username/password: configured in `docker-compose.yml`

### Step 3: Run backend

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

- `http://localhost:8080`

### Step 4: Run frontend

In a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

- `http://localhost:5173`

### Step 5: Login

Default seeded credentials:

- Username: `admin`
- Password: `admin123`

---

## 3) Production Deployment

Production generally consists of:

- PostgreSQL database (managed service or VM/container)
- Spring Boot backend running as a service/container
- React frontend built as static files and served via Nginx (or similar)

### 3.1 Backend production configuration

Use environment variables (or externalized config) instead of hardcoded values.

Recommended variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SERVER_PORT` (optional, default `8080`)
- `APP_CORS_ALLOWED_ORIGINS` (comma-separated, default `http://localhost:5173`)

Example:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://<db-host>:5432/familytree'
export SPRING_DATASOURCE_USERNAME='<db-user>'
export SPRING_DATASOURCE_PASSWORD='<db-password>'
export SERVER_PORT=8080
export APP_CORS_ALLOWED_ORIGINS='https://your-frontend-domain.com'
```

Build backend jar:

```bash
cd backend
mvn clean package -DskipTests
```

Run backend jar:

```bash
java -jar target/familytree-0.0.1-SNAPSHOT.jar
```

### 3.2 Frontend production build

If backend is hosted under a domain/path different from local dev, update API base URL handling in frontend configuration before build.

Build frontend:

```bash
cd frontend
npm install
npm run build
```

This creates static assets in `frontend/dist`.

### 3.3 Serve frontend via Nginx (example)

Copy `frontend/dist` contents to your web root (example: `/var/www/familytree`).

Example Nginx server block:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/familytree;
    index index.html;

    location / {
        try_files $uri /index.html;
    }

    # Proxy API calls to Spring Boot
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3.4 Process management for backend

Use a process manager so backend restarts on failure/reboot.

Common options:

- systemd service
- Docker container with restart policy
- Kubernetes deployment

---

## 4) Recommended Production Hardening

- Change default credentials immediately.
- Store secrets in a secure secret manager.
- Enable HTTPS with TLS certificates.
- Restrict database network access (private subnet/security groups).
- Enable application logs and monitoring.
- Configure backups for PostgreSQL.

---

## 5) Basic Verification Checklist

After deployment, verify:

1. Frontend loads successfully.
2. Login API works (`POST /api/auth/login`).
3. You can create a family tree.
4. You can add/remove members.
5. Backend can read/write to PostgreSQL.
