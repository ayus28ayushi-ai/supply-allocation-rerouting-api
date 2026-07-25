# DERA (Dynamic Emergency Rerouting & Allocation System)

⚠️ **Status: Under Active Development** ⚠️

---

* ### Current Implementation

* **Database Architecture:** PostgreSQL with Flyway migration scripts and `@Version` schema columns.
* **Data Access & Mapping:** Spring Data JPA method conventions with null-safe DTO mapping and custom stock lookup queries.
* **  Rerouting Engine:** Proximity-sorted candidate evaluation via Haversine math with an automatic multi-warehouse fallback loop.
* **Hybrid Concurrency Control:**
  * **Pessimistic Locks (`FOR UPDATE`):** Single-item row locks on order allocations and cancellation restocks to prevent double-allocations and lost updates.
  * **Optimistic Locks (`@Version`):** DTO version-matching on admin stock updates to prevent stale web form overwrites.
* **API & DTO Layer:** 10 REST endpoints with request validation and centralized global exception handling (Tested using Postman)

  ...
