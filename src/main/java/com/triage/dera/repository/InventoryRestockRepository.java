package com.triage.dera.repository;

import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.InventoryRestock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRestockRepository extends JpaRepository<InventoryRestock, Long> {

    Optional<InventoryRestock> findTopByItemId_ItemIdOrderByIdDesc(Long itemId);

    List<InventoryRestock> findByItemId_ItemIdOrderByIdDesc(Long itemId);
}
