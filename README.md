# Blood Matcher — Smart Blood Donor and Recipient Matching System

> **A full-stack, enterprise-grade emergency blood donor and recipient matching platform.**
> Built with Java 21, Spring Boot 3.3.4, React 18, TypeScript, Vite, Aiven MySQL, Railway, and Vercel.

---

## 1. System Architecture

```
                                  +-----------------------------+
                                  |     Vercel (Frontend)       |
                                  |   React + TypeScript + Vite |
                                  |   https://<app>.vercel.app  |
                                  +--------------+--------------+
                                                 |
                                                 | HTTPS / REST APIs
                                                 v
+-------------------------------+ +-----------------------------+
|    Aiven Cloud (Database)     | |      Railway (Backend)      |
|    Managed MySQL 8.x with TLS |<+  Spring Boot 3.3.4 (Java 21)|
|    Port 3306 (sslMode=REQUIRED) | |  Docker Container on $PORT  |
+-------------------------------+ +-----------------------------+
```

- **Frontend**: Single Page Application built with React 18, TypeScript, Vite, and Lucide React. Hosted on **Vercel**.
- **Backend**: RESTful microservice built with Spring Boot 3.3.4, Spring Security 6 with JWT authentication, Spring Data JPA, Hibernate, and Flyway database migrations. Hosted on **Railway**.
- **Database**: Managed high-availability MySQL hosted on **Aiven** with enforced TLS/SSL encryption.

---

## 2. Repository Layout

```
BloodSharing/
├── .gitignore                   # Workspace root Git ignore rules
├── README.md                    # System architecture & deployment guide
├── backend/                     # Spring Boot application
│   ├── Dockerfile               # Multi-stage production container build
│   ├── pom.xml                  # Maven dependencies & build plugins
│   ├── railway.json             # Railway container deployment configuration
│   ├── .env.example             # Template for backend environment variables
│   ├── src/                     # Java source code and database migrations
│   └── README.md                # Detailed backend documentation & API reference
└── frontend/                    # React + TypeScript frontend
    ├── package.json             # Scripts & UI dependencies
    ├── vite.config.ts           # Vite bundler & dev server reverse proxy
    ├── vercel.json              # Vercel SPA routing rewrites
    ├── .env.example             # Template for frontend environment variables
    ├── src/                     # React components, hooks, features, and styles
    └── README.md                # Frontend architecture and usage instructions
```

---

## 3. Local Development Setup

### Prerequisites
- Java 21 SDK
- Node.js (v18+)
- MySQL (or Dockerized MySQL)

### Running Backend Locally
```bash
cd backend
# Run with local database or embedded H2/MySQL
./mvnw spring-boot:run
```
Backend will start on `http://localhost:8080`. API documentation available at `http://localhost:8080/swagger-ui.html`.

### Running Frontend Locally
```bash
cd frontend
npm install
npm run dev
```
Frontend will run on `http://localhost:3000` (or `5173`) and automatically proxy `/api` calls to `http://localhost:8080`.

---

## 4. Production Cloud Deployment Guide

### A. Database Provisioning (Aiven MySQL)
1. Sign in to your [Aiven Console](https://console.aiven.io/).
2. Create a new service: **MySQL** (version 8.x).
3. Under Service Overview, copy the connection details:
   - **Host**
   - **Port** (e.g., `12345`)
   - **User** (e.g., `avnadmin`)
   - **Password**
   - **Database Name** (default is `defaultdb`)
   - **SSL Mode**: Required (`sslMode=REQUIRED`)
4. Flyway automatically runs database migrations upon backend startup.

### B. Backend Deployment (Railway)
1. Sign in to [Railway](https://railway.app/) using GitHub account **`jathinxyz`**.
2. Click **+ New Project** -> **Deploy from GitHub repo** -> select `jathinxyz/BloodSharing`.
3. In Service Settings:
   - Set **Root Directory** to `/backend`.
   - Set **Builder** to `DOCKERFILE` (uses `backend/Dockerfile`).
4. Configure Environment Variables in Railway:
   | Variable | Value | Description |
   |---|---|---|
   | `PORT` | `8080` (or leave default assigned by Railway) | Container listening port |
   | `SPRING_DATASOURCE_URL` | `jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/defaultdb?sslMode=REQUIRED` | Aiven MySQL JDBC connection URL |
   | `DB_USERNAME` | `<AIVEN_USER>` | Aiven username |
   | `DB_PASSWORD` | `<AIVEN_PASSWORD>` | Aiven password |
   | `JWT_SECRET` | Strong 256-bit string (min 32 characters) | Secret key for JWT signature |
   | `APP_CORS_ALLOWED_ORIGINS` | `https://<YOUR_VERCEL_APP>.vercel.app,https://*.vercel.app,http://localhost:5173` | Allowed frontend URLs and preview deployments |
   | `ADMIN_EMAIL` | `admin@bloodmatcher.org` | Initial bootstrap admin email |
   | `ADMIN_PASSWORD` | Strong password (e.g. 16+ chars) | Initial bootstrap admin password |
5. Click **Deploy**. Once healthy, generate a public Railway domain under **Networking** (e.g. `https://bloodsharing-production.up.railway.app`).
6. Test backend health: Open `https://<YOUR_RAILWAY_URL>/actuator/health` in your browser. It should respond with `{"status":"UP"}`.

### C. Frontend Deployment (Vercel)
1. Sign in to [Vercel](https://vercel.com/) with GitHub account **`jathinxyz`**.
2. Click **Add New...** -> **Project** -> import `jathinxyz/BloodSharing`.
3. In Project Configuration:
   - **Root Directory**: `frontend`
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. In **Environment Variables**:
   - `VITE_API_BASE_URL`: `https://<YOUR_RAILWAY_BACKEND_DOMAIN>` (e.g. `https://bloodsharing-production.up.railway.app` without trailing slash)
5. Click **Deploy**. Vercel will build and assign your production URL.
6. Copy the assigned Vercel domain and verify that it matches or is included in Railway's `APP_CORS_ALLOWED_ORIGINS`.

---

## 5. Deployment Verification Checklist

Once both services are running:
1. **Database & Schema**: Check Railway deployment logs to confirm Flyway completed migration `V1__init_schema.sql` successfully.
2. **Backend Health**: Visit `https://<RAILWAY_URL>/actuator/health` (returns `{"status":"UP"}`).
3. **Public API Connectivity**: Visit `https://<RAILWAY_URL>/api/public/info` (returns system version and platform metrics).
4. **Frontend Delivery**: Visit `https://<VERCEL_URL>/` and verify:
   - Emergency banner displays India national emergency helpline `112`.
   - Navigation links (How it works, Compatibility matrix, Disclaimers) work without 404s.
5. **End-to-End Auth**: Test registering a donor/recipient or signing in with the bootstrap admin account to confirm frontend-to-backend CORS and JWT issuance.

---

## 6. Medical Safety & Privacy Notice

> **IMPORTANT DISCLAIMER**:
> This platform provides **preliminary matching assistance only**.
> - **In Medical Emergencies**: Call emergency services (e.g., 112 in India) or proceed immediately to the nearest hospital emergency room.
> - **Screening & Compatibility**: All donor eligibility screening, infectious disease testing, ABO/Rh cross-matching, and transfusion decisions must be executed by certified medical personnel and licensed blood banks.
> - **Donor Privacy**: Donor contact information is never published publicly and is disclosed only when a donor explicitly accepts an urgent matching invitation.
