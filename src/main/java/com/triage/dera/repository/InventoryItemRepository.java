package com.triage.dera.repository;

import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findByItemNameAndWarehouseWarehouseId(String itemName, Long reqWarehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<InventoryItem> findByItemNameAndQuantityAvailableGreaterThanEqualAndWarehouseWarehouseIdNot(
            String itemName,
            Integer quantityRequested,
            Long primWarehouseId
    );
}
