# DERA (Dynamic Emergency Rerouting & Allocation System)

🚧 **Status: Under Active Development** 🚧

---

### 📌 Current Implementation

* **Database Architecture:** Schema management using PostgreSQL and Flyway migrations.
* **Data Access & Mappings:** Spring Data JPA entities and custom stock lookup queries.
* **Fulfillment & Rerouting Engine:** Core pipeline that auto-reroutes orders to alternative warehouses when primary stock is depleted.
* **Concurrency Control:** Hybrid locking to prevent race conditions during concurrent stock updates.
* **API & DTO Layer:** REST endpoints for allocations, cancellation with stock restoration, and dedicated DTOs for request validation.
