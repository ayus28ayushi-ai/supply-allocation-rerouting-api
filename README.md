# DERA (Dynamic Emergency Rerouting & Allocation System)

🚧 **Status: Under Active Development** 🚧

---

### 📌 Current Implementation

- **Database Architecture:** Schema management and version control using PostgreSQL and Flyway migrations.
- **Data Access & Mappings:** Spring Data JPA entity mappings with custom query methods for stock lookup.
- **Fulfillment & Rerouting Engine:** Core allocation pipeline that processes incoming requests and automatically reroutes orders to alternative warehouses when primary stock is depleted.
