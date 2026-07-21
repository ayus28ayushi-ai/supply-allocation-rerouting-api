package com.triage.dera.repository;

import com.triage.dera.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByItemNameAndWarehouseWarehouseId(String itemName, Long reqWarehouseId);
    List<InventoryItem> findByItemNameAndQuantityAvailableGreaterThanEqualAndWarehouseWarehouseIdNot(
            String itemName,
            Integer quantityRequested,
            Long primWarehouseId
    );
}
