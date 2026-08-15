![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-Migration-CC0200?style=flat-square&logo=flyway&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Stateless-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20%26%20GitHub-4285F4?style=flat-square&logo=google&logoColor=white)
![Swagger UI](https://img.shields.io/badge/Swagger-OpenAPI%203.0-85EA2D?style=flat-square&logo=swagger&logoColor=black)

# DERA (Dynamic Emergency Rerouting & Allocation System)

DERA is a warehouse inventory management and order allocation system built with Spring Boot 3 and Java 21. It features intelligent dynamic rerouting using the **Haversine formula**, robust hybrid database concurrency control, stateless JWT authentication, and OAuth2 social logins.

<details>
<summary><b>Table of Contents</b></summary>

- [Live Demo & Documentation](#live-demo--documentation)
- [ Key Architectural Highlights](#key-architectural-highlights)
- [ System Architecture Diagrams](#system-architecture-diagrams)
- [ API Endpoint Reference](#api-endpoint-reference)
- [ Seed Admin Credentials](#seed-admin-credentials)
- [Sample Test Payloads](#sample-test-payloads)
- [ Step-by-Step Testing Guide](#step-by-step-testing-guide)
- [ Local Setup & Installation](#local-setup--installation)

</details>

---

## Live Demo & Documentation

* **Live Deployment:** `[YOUR_LIVE_DEPLOYMENT_URL_HERE]` *(Deployment Pending)*
* **Interactive Swagger UI:** `[YOUR_LIVE_DEPLOYMENT_URL_HERE]/swagger-ui/index.html`  
* **Demo Video:** `[YOUR_DEMO_VIDEO_LINK_HERE]`

---
## Key Architectural Highlights

### 1. Haversine Dynamic Rerouting Engine
* Calculates geographical distance (lat/long) to alternate warehouses upon primary stock deficits.
* Sorts candidates by proximity and automatically reroutes allocations.

### 2. Hybrid Database Concurrency Control
* **Pessimistic Locking (`FOR UPDATE`):** Applied to single-item order allocations and cancellation restocks to guarantee thread safety, preventing double allocations and lost updates under heavy concurrent traffic.
* **Optimistic Locking (`@Version`):** Utilized on inventory items and warehouse stock entities to detect concurrent administrative modifications and block stale form overwrites.

### 3. Automated Database Schema Versioning (Flyway)
* Uses **Flyway** migration scripts (`V1__...sql`, `V2__...sql`) to automatically version, create, and update PostgreSQL tables upon application startup.
* Eliminates manual DDL script execution and guarantees consistent database schema across developer environments.

### 4. Pagination & Sorting
* Built-in support for **Spring Data `Pageable`** across heavy administrative endpoints.
* Enables query-level pagination (`page`, `size`, `sort`) to maintain low memory footprints and fast response times on large datasets.

### 5. Comprehensive Security Architecture
* **Stateless Authentication:** Dual support for native DAO Authentication (Email/Password) via JWT tokens and **OAuth2 (Google & GitHub)** social logins.
* **Role-Based Access Control (RBAC):** Fine-grained permission checks distinguishing `ROLE_USER` and `ROLE_ADMIN` endpoints via `@PreAuthorize("hasRole('ADMIN')")`.
* **Automated Data Seeding:** Startup initialization via `CommandLineRunner` to seed a default Super Admin account if none exists.
---

## System Architecture Diagrams
### End-to-End Application & Dynamic Rerouting Flow
```mermaid
flowchart TD
    %% User Layer
    subgraph Client ["Client & Frontend Layer"]
        User["👤 End User / Tester"]
        AuthUI["🎨 Auth UI Portal<br/>(Sign In / Register)"]
        SwaggerUI["📄 Swagger UI<br/>(API Testing & Docs)"]
    end

    %% Auth & Identity
    subgraph AuthLayer ["Identity & Access Management"]
        AuthService["🔐 Auth Controller"]
        OAuth["🌐 External OAuth<br/>(Google / GitHub)"]
        JWT["🔑 JWT Token Provider"]
    end

    %% API Gateway & Middleware
    subgraph Gateway ["API Gateway & Middleware"]
        AuthFilter["🛡️ Bearer Token Filter"]
        Router["🔀 API Endpoint Router"]
    end

    %% Core Application Layer
    subgraph DeraCore ["Dera Core System"]
        UserSvc["👤 User Service<br/>(/v1/user/profile-check)"]
        RerouteEngine["🚨 Dynamic Emergency Rerouting"]
        AllocEngine["📦 Resource Allocation Engine"]
    end

    %% Data Layer
    subgraph DataLayer ["Storage Layer"]
        DB[(🗄️ Database)]
        CentralHub["🏭 Central Warehouse Hub"]
    end

    %% Flow Steps
    User -->|1. Sign In / Register| AuthUI
    AuthUI -->|2a. Email/Password Auth| AuthService
    AuthUI -->|2b. Social Auth| OAuth
    OAuth -->|Callback| AuthService
    
    AuthService -->|3. Generate JWT| JWT
    JWT -->|4. Redirect with ?token=| SwaggerUI

    User -->|5. Authorize with Bearer Token| SwaggerUI
    SwaggerUI -->|6. Call Endpoint| AuthFilter

    AuthFilter -->|7. Verify JWT| JWT
    AuthFilter -->|8. Dispatch Authorized Request| Router

    Router --> UserSvc
    Router --> RerouteEngine
    Router --> AllocEngine

    UserSvc --> DB
    RerouteEngine -->|Fetch Logistics Data| DB
    AllocEngine -->|Manage Allocations| CentralHub
    CentralHub --> DB

    %% Styling
    classDef clientStyle fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;
    classDef authStyle fill:#fff3e0,stroke:#f57c00,stroke-width:2px;
    classDef coreStyle fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef dbStyle fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px;

    class User,AuthUI,SwaggerUI clientStyle;
    class AuthService,OAuth,JWT authStyle;
    class AuthFilter,Router,UserSvc,RerouteEngine,AllocEngine coreStyle;
    class DB,CentralHub dbStyle;
```
---
### Dual-Track Auth & Security Filter Chain
```mermaid
flowchart LR
    %% 1. Entry Points
    subgraph IN ["1. Client Requests"]
        ReqLogin["POST /dera/login<br/>(Email & Password)"]
        ReqOAuth["GET /oauth2/authorization/...<br/>(Google / GitHub)"]
        ReqAPI["Protected API Request<br/>(Header: Bearer Token)"]
    end

    %% 2. Auth Mechanisms
    subgraph AUTH ["2. Spring Security Authentication"]
        subgraph DAO ["DAO Auth Track"]
            DaoFilter["UsernamePassword<br/>AuthenticationFilter"]
            AuthMgr["AuthenticationManager"]
            DaoProv["DaoAuthenticationProvider"]
            UserSvc["CustomUserDetailsService"]
            BCrypt["BCryptPasswordEncoder"]

            DaoFilter --> AuthMgr --> DaoProv
            DaoProv --> UserSvc
            DaoProv --> BCrypt
        end

        subgraph OAUTH ["OAuth2 Social Track"]
            OAuthFilter["OAuth2Login<br/>AuthenticationFilter"]
            OAuthProv["Google / GitHub OAuth Provider"]
            OAuthSvc["CustomOAuth2UserService"]

            OAuthFilter --> OAuthProv --> OAuthSvc
        end
    end

    %% 3. JWT Service & Security Keys
    subgraph KEYMGMT ["3. JwtService & Security Keys"]
        GetKey["🔑 Base64 HMAC Key Resolver<br/> "]
        GenToken["🎟️ Issue & Sign JWT Token"]
        JwtFilt["🛡️ JwtAuthenticationFilter"]
        JwtVal{"Verify Token Signature"}

        GetKey -->|Provides Signing Key| GenToken
        GetKey -->|Provides Verification Key| JwtVal
        JwtFilt --> JwtVal
    end

    %% 4. Context & RBAC Execution
    subgraph EXEC ["4. Context & Execution"]
        SecCtx["Set SecurityContextHolder<br/>(Principal + Granted Authorities)"]
        PreAuth{"@PreAuthorize Check<br/>hasRole('ADMIN') / 'USER'"}
        AdminCtrl["⚙️ Admin Controllers<br/>(Stock, Warehouse, Audits)"]
        UserCtrl["📦 User Controllers<br/>(Allocations, Catalog)"]

        SecCtx --> PreAuth
        PreAuth -->|"ROLE_ADMIN"| AdminCtrl
        PreAuth -->|"ROLE_USER"| UserCtrl
    end

    %% Linking Flow Pathways
    ReqLogin --> DaoFilter
    ReqOAuth --> OAuthFilter

    DAO --> GenToken
    OAUTH --> GenToken

    ReqAPI --> JwtFilt
    JwtVal -->|"Valid Signature"| SecCtx

    %% Styling
    style ReqLogin fill:#1e293b,color:#fff,stroke:#475569
    style ReqOAuth fill:#1e293b,color:#fff,stroke:#475569
    style ReqAPI fill:#1e293b,color:#fff,stroke:#475569
    style DaoFilter fill:#312e81,color:#fff,stroke:#4338ca
    style AuthMgr fill:#312e81,color:#fff,stroke:#4338ca
    style DaoProv fill:#312e81,color:#fff,stroke:#4338ca
    style UserSvc fill:#312e81,color:#fff,stroke:#4338ca
    style BCrypt fill:#312e81,color:#fff,stroke:#4338ca
    style OAuthFilter fill:#701a75,color:#fff,stroke:#a21caf
    style OAuthProv fill:#701a75,color:#fff,stroke:#a21caf
    style OAuthSvc fill:#701a75,color:#fff,stroke:#a21caf
    style GetKey fill:#b45309,color:#fff,stroke:#f59e0b
    style GenToken fill:#065f46,color:#fff,stroke:#047857
    style JwtFilt fill:#065f46,color:#fff,stroke:#047857
    style JwtVal fill:#065f46,color:#fff,stroke:#047857
    style SecCtx fill:#854d0e,color:#fff,stroke:#a16207
    style PreAuth fill:#1e3a8a,color:#fff,stroke:#1d4ed8
    style AdminCtrl fill:#581c87,color:#fff,stroke:#9333ea
    style UserCtrl fill:#047857,color:#fff,stroke:#10b981
```

---

## API Endpoint Reference

### User & Authentication (`user-controller`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/dera/register` | Public | Register a new standard user |
| `POST` | `/dera/login` | Public | Authenticate user & retrieve JWT token |
| `POST` | `/dera/admin/register-admin` | Admin | Onboard a new administrator account |

### Inventory Management (`inventory-item-controller`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/dera/admin/stock` | Admin | Add or restock inventory items |
| `PUT` | `/dera/admin/item/{itemId}/details` | Admin | Update item metadata |
| `PATCH` | `/dera/admin/item/{itemId}` | Admin | Partial updates to inventory item |
| `GET` | `/dera/admin/inventory` | Admin | View all inventory stock levels |
| `GET` | `/dera/admin/item/search` | Admin | Search inventory items with criteria |
| `GET` | `/dera/admin/item/{itemId}` | Admin | Fetch single item details by ID |
| `GET` | `/dera/user/inventory` | User/Admin | Browse public inventory list |
| `GET` | `/dera/user/item/{itemId}` | User/Admin | View item details |
| `GET` | `/dera/user/warehouse/{warehouseId}` | User/Admin | List inventory by specific warehouse |
| `GET` | `/dera/admin/warehouse/{warehouseId}` | Admin | Administrative warehouse inventory lookup |

### Order Allocation & Rerouting (`allocation-record-controller`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/dera/user/allocation` | User | Request stock allocation (Triggers Haversine reroute if out of stock) |
| `PATCH` | `/dera/user/allocation/{allocationId}/cancel` | User | Cancel an allocation record & trigger pessimistic restock |
| `GET` | `/dera/user/allocation/{allocationId}` | User | Get allocation details by ID |
| `GET` | `/dera/user/allocation/war/{warId}` | User | View allocations for a specific warehouse |
| `GET` | `/dera/admin/allocation` | Admin | Get paginated list of all allocations |
| `GET` | `/dera/admin/allocation/item/{itemId}` | Admin | View allocation history for a specific item |

### Warehouse Operations (`warehouse-controller`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/dera/admin/warehouse` | Admin | Register a new warehouse with geo-coordinates |
| `PATCH` | `/dera/admin/warehouse/{wareId}` | Admin | Update warehouse location or metadata |
| `GET` | `/dera/user/warehouses` | User/Admin | Get list of active warehouses |
| `GET` | `/dera/user/warehouse/{wareId}` | User/Admin | Get specific warehouse details |
---

## Seed Admin Credentials

An initial admin account is automatically seeded into PostgreSQL upon application startup.
Test out the admin endpoints with it.
(Only an admin can add another admin)

| Field | Value                           |
| :--- |:--------------------------------|
| **Email** | `ayu.s28.ayushisingh@gmail.com` |
| **Password** | `sunflower`                     |
| **Role** | `ROLE_ADMIN`                    |
---
## Step-by-Step Testing Guide

Follow these steps to authenticate and test protected endpoints using the Auth Portal and Swagger UI:

1. **Access the Auth Portal:**
   Navigate to the portal URL


2. **Authenticate (Register or Login):**  
  * **New User:** Switch to the **Register** tab to create a new `ROLE_USER` account.
  * **Existing User / Admin:** Use the **Sign In** tab with valid credentials, or choose **Google** / **GitHub** OAuth2.

    <img src="media/readmeIMG/img_3.png"  width="320px" height="180px" />


3. **Retrieve Your JWT Token:**
  * Upon successful authentication, you will be automatically redirected to the Swagger UI page.
  * Look at your browser address bar and copy the JWT token value from the URL query parameter (e.g., `.../swagger-ui/index.html?token=YOUR_JWT_TOKEN`).
 
    <img src="media/readmeIMG/img_2.png"  width="600px" height="60px" />

4. **Authorize Swagger UI:**
  * Click the green **Authorize** 🔓 button at the top right of the Swagger UI interface.
  * Paste your copied token into the **Value** field (format: `Bearer YOUR_JWT_TOKEN` or your raw token string) and click **Authorize**, then **Close**.

<img src="media/readmeIMG/img_1.png"  width="200px" height="80px" />
<img src="media/readmeIMG/img.png"  width="300px" height="80px" />

5. **Test Endpoints:**
  * You are now authenticated. Expand any endpoint and click **Try it out** to send requests based on your account's assigned role (`ROLE_USER` or `ROLE_ADMIN`).

---
## Sample Test Payloads
Use the sample JSON bodies in the PDF to quickly test endpoints in Swagger UI or Postman

<a href="src/main/resources/static/DeraAPI_sample_payload.pdf" target="_blank">Click to Open PDF</a>

---
## Local Setup & Installation

Follow these steps to configure, build, and run the project on your local machine.

### Prerequisites
* **Java 21** (or 17+)
* **Maven 3.8+**
* **PostgreSQL** database running locally 

---

### 1. Clone the Repository

```bash
git clone https://github.com/ayus28ayushi-ai/supply-allocation-rerouting-api.git
```

### 2. Configure Environment Variables

⚠️ **Important:** Update ***src/main/resources/application.properties*** with your actual database credentials and API keys before running the application. Make sure to add application.properties to your .gitignore if it contains sensitive secrets.

Open ***src/main/resources/application.properties*** and replace the placeholder values:
```env
spring.datasource.password=${POSTGRE_PASSWORD}
jwt.secret=${JWT_SECRET_KEY}

app.admin.email=${ADMIN_EMAIL}
app.admin.password=${ADMIN_PASSWORD}

spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
```

### 3. Database Initialization

Ensure PostgreSQL is running and create the target database:

```sql
CREATE DATABASE triage_dera_db;
```

### 4. Build and Run the Application

#### Clean and build the project

```bash
./mvnw clean package -DskipTests
```

#### Launch the Spring Boot application

```bash
./mvnw spring-boot:run
```

### 5. Verify Setup

Once the application has started, access the following URLs in your browser:

* **Auth Portal:** http://localhost:8080/login.html
---
## Author
  ***Name*** : Ayushi Singh  
  ***GitHub Profile*** : https://github.com/ayus28ayushi-ai