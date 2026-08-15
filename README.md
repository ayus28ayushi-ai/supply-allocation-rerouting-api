# DERA (Dynamic Emergency Rerouting & Allocation System)
---
DERA is a warehouse inventory management and order allocation system built with Spring Boot 3 and Java 21. It features intelligent dynamic rerouting using the **Haversine formula**, robust hybrid database concurrency control, stateless JWT authentication, and OAuth2 social logins.

---

##  Live Demo & Documentation

* **Live Deployment:** `[YOUR_LIVE_DEPLOYMENT_URL_HERE]` *(Deployment Pending)*
* **Interactive Swagger UI:** `[YOUR_LIVE_DEPLOYMENT_URL_HERE]/swagger-ui/index.html` *(Local: `http://localhost:8080/swagger-ui/index.html`)*
* **Demo Video:** `[YOUR_DEMO_VIDEO_LINK_HERE]`

---

## 🏗 Key Architectural Highlights

### 1. Haversine Dynamic Rerouting Engine
* Calculates geographical distance (lat/long) to alternate warehouses upon primary stock deficits.
* Sorts candidates by proximity and automatically reroutes allocations.

### 2. Hybrid Database Concurrency Control
* **Pessimistic Locking (`FOR UPDATE`):** Applied to single-item order allocations and cancellation restocks to guarantee thread safety, preventing double allocations and lost updates under heavy concurrent traffic.
* **Optimistic Locking (`@Version`):** Utilized on inventory items and warehouse stock entities to detect concurrent administrative modifications and block stale form overwrites.

### 3. Comprehensive Security Architecture
* **Stateless Authentication:** Dual support for native DAO Authentication (Email/Password) via JWT tokens and **OAuth2 (Google & GitHub)** social logins.
* **Role-Based Access Control (RBAC):** Fine-grained permission checks distinguishing `ROLE_USER` and `ROLE_ADMIN` endpoints via `@PreAuthorize("hasRole('ADMIN')")`.
* **Automated Data Seeding:** Startup initialization via `CommandLineRunner` to seed a default Super Admin account if none exists.


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

## 📋 API Endpoint Reference

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

## 🗝 Seed Admin Credentials

An initial admin account is automatically seeded into PostgreSQL upon application startup.
Test out the admin endpoints with it.
(Only an admin can add another admin)

| Field | Value                           |
| :--- |:--------------------------------|
| **Email** | `ayu.s28.ayushisingh@gmail.com` |
| **Password** | `sunflower`                     |
| **Role** | `ROLE_ADMIN`                    |
---
## 🧪 Step-by-Step Testing Guide

Follow these steps to authenticate and test protected endpoints using the Auth Portal and Swagger UI:

1. **Access the Auth Portal:**
   Navigate to the portal URL

    


2. **Authenticate (Register or Login):**  
  * **New User:** Switch to the **Register** tab to create a new `ROLE_USER` account.
  * **Existing User / Admin:** Use the **Sign In** tab with valid credentials, or choose **Google** / **GitHub** OAuth2.

   <img src="readmeIMG/img_3.png"  width="320px" height="180px" />


3. **Retrieve Your JWT Token:**
  * Upon successful authentication, you will be automatically redirected to the Swagger UI page.
  * Look at your browser address bar and copy the JWT token value from the URL query parameter (e.g., `.../swagger-ui/index.html?token=YOUR_JWT_TOKEN`).


<img src="readmeIMG/img_2.png"  width="600px" height="60px" />

4. **Authorize Swagger UI:**
  * Click the green **Authorize** 🔓 button at the top right of the Swagger UI interface.
  * Paste your copied token into the **Value** field (format: `Bearer YOUR_JWT_TOKEN` or your raw token string) and click **Authorize**, then **Close**.

<img src="readmeIMG/img_1.png"  width="200px" height="80px" />
<img src="readmeIMG/img.png"  width="300px" height="80px" />

5. **Test Endpoints:**
  * You are now authenticated. Expand any endpoint and click **Try it out** to send requests based on your account's assigned role (`ROLE_USER` or `ROLE_ADMIN`).

---
## 🧪 Sample Test Payloads
Use the sample JSON bodies in the PDF to quickly test endpoints in Swagger UI or Postman

---
 
  
