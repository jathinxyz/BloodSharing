# Blood Matcher — Frontend Application

Modern, high-performance, and responsive React + TypeScript frontend for **Blood Matcher — Smart Blood Donor and Recipient Matching System**.

---

## 1. Technology Stack

- **Framework**: React 18 with TypeScript
- **Build Tool & Dev Server**: Vite
- **Routing**: React Router DOM (v6)
- **Icons**: Lucide React
- **Styling**: Pure CSS Design System (`src/styles/index.css`) featuring custom CSS variables, neutral slate surfaces, deep red healthcare accents, responsive grid/flexbox layouts, accessible contrast, and smooth micro-interactions.
- **HTTP Client**: Native `fetch` with token storage, automatic JWT refresh rotation on HTTP 401, error parsing, and backend API contract synchronization.

---

## 2. Directory Structure

The project strictly follows the required architecture:

```
frontend/
├── index.html                     # HTML5 entrypoint with Google Fonts (Inter)
├── package.json                   # Dependencies & build scripts
├── tsconfig.json                  # TypeScript compiler options & alias (@/*)
├── tsconfig.node.json             # Node environment configuration for Vite
├── vite.config.ts                 # Vite setup with @ alias and /api proxy (port 8080)
├── public/
│   └── favicon.svg                # Red blood droplet brand icon
└── src/
    ├── main.tsx                   # React root mount with AuthProvider
    ├── vite-env.d.ts              # Vite environment typings
    ├── app/
    │   └── App.tsx                # Client-side router table & protected routes
    ├── components/                # Reusable design system components
    │   ├── BloodGroupBadge.tsx    # ABO & Rh blood group badges
    │   ├── EmergencyBanner.tsx    # Prominent medical emergency disclaimer
    │   ├── Footer.tsx             # Standard healthcare footer with regulatory notes
    │   ├── HealthDisclaimerCard.tsx # Laboratory cross-matching safety card
    │   ├── LoadingSpinner.tsx     # Clean loading indicator
    │   ├── Modal.tsx              # Accessible dialog modal
    │   ├── Navbar.tsx             # Responsive header with role-based navigation
    │   └── StatusBadge.tsx        # Color-coded badge for status/urgency
    ├── features/                  # Domain-specific feature modules
    │   ├── admin/
    │   │   └── AdminDashboard.tsx # Platform metrics, user controls & request oversight
    │   ├── auth/
    │   │   ├── AuthContext.tsx    # Global session provider & token manager
    │   │   └── ProtectedRoute.tsx # Route guard enforcing authentication & roles
    │   ├── donor/
    │   │   └── DonorDashboard.tsx # Profile, availability toggle, invitations & donation history
    │   ├── matching/
    │   │   ├── MatchModal.tsx     # Algorithm results preview & invitation dispatch
    │   │   └── RequestInvitationsModal.tsx # Inspection of sent invitations
    │   ├── notifications/
    │   │   └── NotificationBell.tsx # Unread notification badge with counter
    │   └── requests/
    │       ├── CreateRequestModal.tsx # Blood request creation with GPS coordinates
    │       └── RecipientDashboard.tsx # Recipient request management & fulfillment
    ├── hooks/
    │   ├── useAuth.ts             # Auth context consumer
    │   └── useGeolocation.ts      # Browser Geolocation API wrapper
    ├── layouts/
    │   └── PublicLayout.tsx       # Root layout wrapping Navbar, EmergencyBanner & Footer
    ├── pages/                     # Routed pages
    │   ├── CompatibilityPage.tsx  # Blood group compatibility matrix & rules
    │   ├── DisclaimersPage.tsx    # Comprehensive legal & medical disclaimers
    │   ├── HowItWorksPage.tsx     # Donor & recipient step-by-step workflow guide
    │   ├── LandingPage.tsx        # Hero, real-time platform statistics & call-to-actions
    │   ├── LoginPage.tsx          # JWT authentication form
    │   ├── NotFoundPage.tsx       # 404 error page
    │   ├── NotificationsPage.tsx  # User notification inbox
    │   └── RegisterPage.tsx       # Role-aware registration (Donor / Recipient)
    ├── services/                  # Backend REST API integration
    │   ├── adminService.ts        # Admin endpoints
    │   ├── apiClient.ts           # Token storage & authenticated fetch client
    │   ├── authService.ts         # Login, register, logout, me, refresh
    │   ├── donorService.ts        # Donor profile, availability, invitations & donations
    │   ├── notificationService.ts # Notification management
    │   ├── publicService.ts       # Public info, disclaimers & compatibility matrix
    │   └── requestService.ts      # Blood request CRUD, matching & fulfillment
    ├── styles/
    │   └── index.css              # Custom medical design system & utility classes
    ├── types/
    │   └── index.ts               # TypeScript DTOs mirroring Spring Boot models
    └── utils/
        ├── bloodGroups.ts         # ABO/Rh compatibility rules & blood group constants
        └── formatters.ts          # Date, distance, and string formatting helpers
```

---

## 3. Getting Started

### Prerequisites

- Node.js (v18 or higher recommended)
- The Spring Boot backend running on `http://localhost:8080`

### Installation

Inside the `frontend/` directory, install the required dependencies:

```bash
npm install
```

### Running the Development Server

Start Vite with hot module replacement:

```bash
npm run dev
```

The frontend will run at `http://localhost:5173`. Any API calls to `/api/*` are automatically proxied to `http://localhost:8080`.

### Building for Production

Compile TypeScript and build the optimized production bundle:

```bash
npm run build
```

The production assets are generated in `frontend/dist/`.

### Previewing the Production Build

```bash
npm run preview
```

---

## 4. Key Functional Features

### 1. Transparent Medical Disclaimers & Emergency Notice
- **Emergency Notice**: Displayed on all pages to ensure patients in immediate medical danger call emergency services (e.g., 112 or local emergency numbers) rather than waiting for an online response.
- **Safety Disclaimers**: Clarifies that Blood Matcher provides **preliminary screening only**; all donor eligibility, infectious disease testing, and laboratory cross-matching must be performed by authorized medical personnel and certified blood banks.

### 2. Authentication & Authorization
- **Role-Based Access Control**: Supports `DONOR`, `RECIPIENT`, and `ADMIN` roles.
- **JWT Token Management**: Securely handles `accessToken` and `refreshToken` rotation. Intercepts 401 Unauthorized responses to seamlessly refresh sessions before requesting data again.
- **Protected Routing**: Non-authenticated users or unauthorized roles are redirected to `/login` with preservation of return path.

### 3. Donor Experience
- **Profile Completion**: Update phone, address, and GPS coordinates (with one-click browser geolocation detection).
- **Availability Toggle**: Instant toggle to pause or activate donor availability, synced with the backend.
- **Invitation Queue**: Review incoming invitations with patient urgency, hospital location, and distance in kilometers.
- **Donor Privacy Safeguard**: Recipient contact information is only shared after the donor accepts an invitation.
- **Donation History**: Track past blood donations, donation dates, and recipient cases helped.

### 4. Recipient Experience
- **Create Blood Request**: Request 1 to 20 units of compatible blood with urgency indicators (`ROUTINE`, `URGENT`, `CRITICAL`), hospital address, and GPS location.
- **Smart Donor Matching**: Query the backend matching algorithm to preview compatible donors ranked by proximity.
- **Dispatch Invitations**: Select and invite compatible donors with a single click.
- **Track Status**: Inspect sent invitations, check donor response status (`PENDING`, `ACCEPTED`, `DECLINED`), and fulfill or cancel requests once resolved.

### 5. Admin Oversight
- **System KPIs**: Live counts of total donors, active requests, invitations sent, and completed donations.
- **User Management**: Search users and toggle account statuses (`ACTIVE` / `SUSPENDED`).
- **Emergency Request Oversight**: Monitor open urgent and critical blood requests across the system.
- **Audit & Reports**: One-click CSV export of system statistics.

### 6. Public Exploration & Information
- **Interactive Blood Compatibility Matrix**: Check compatibility rules for all 8 major blood groups (A+, A-, B+, B-, AB+, AB-, O+, O-).
- **How It Works**: Visual walkthrough of both the donor journey and the recipient journey.
