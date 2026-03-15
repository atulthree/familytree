# Family Tree Project (React + Spring Boot + PostgreSQL)

This repository contains a starter full-stack project for managing family trees.

## Stack
- Frontend: React (Vite)
- Backend: Spring Boot (Java 17)
- Database: PostgreSQL

## Features
- Login screen with default credentials (`admin` / `admin123`)
- Create multiple family trees
- Add family members with:
  - Name
  - Surname
  - Sex
  - Date of birth
  - Image URL
  - Spouse
  - Parents
  - Children
- Remove members from UI

## Run PostgreSQL
```bash
docker compose up -d postgres
```

## Run backend
```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

## Run frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## API auth
Call `POST /api/auth/login` and send returned token in `X-Auth-Token` header.

## Deployment docs
See [DEPLOYMENT.md](DEPLOYMENT.md) for local and production deployment steps.
