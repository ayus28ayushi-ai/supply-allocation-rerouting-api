# DERA (Dynamic Emergency Rerouting & Allocation System)

⚠️ **Status: Under Active Development** ⚠️

---

* ### Current Implementation

* **Database Architecture:** PostgreSQL with Flyway migration scripts and `@Version` schema columns.
* **Data Access & Mapping:** Spring Data JPA method conventions with null-safe DTO mapping and custom stock lookup queries.
* **Rerouting Engine:** Proximity-sorted candidate evaluation via Haversine math with an automatic multi-warehouse fallback loop.
* **Hybrid Concurrency Control:**
  * **Pessimistic Locks (`FOR UPDATE`):** Single-item row locks on order allocations and cancellation restocks to prevent double-allocations and lost updates.
  * **Optimistic Locks (`@Version`):** DTO version-matching on admin stock updates to prevent stale web form overwrites.
* **API & DTO Layer:** 10 REST endpoints with request validation and centralized global exception handling (Tested using Postman)
* Implemented 10 more REST endpoints across User and Admin controllers for stock management and catalog operations.
* Created new inventory tables and audit log schema in PostgreSQL.
* Added pagination to allocation records and inventory item restock audit history
* **JWT & DAO Authentication:** Implemented stateless API authentication using JWT tokens and Spring Security DAO Authentication Provider.
*  **Authentication Endpoints:** Exposed public APIs for user registration and login, along with a protected endpoint for existing admins to onboard new admin accounts.
* **Admin Data Seeder:** Automated initial Super Admin account setup on application startup using `CommandLineRunner`.
* **Role-Based Security:** Protected admin endpoints using Spring Security's `@PreAuthorize("hasRole('ADMIN')")`.
* **Environment Variables:** Externalized sensitive admin credentials in `application.properties` with local fallbacks.
  
