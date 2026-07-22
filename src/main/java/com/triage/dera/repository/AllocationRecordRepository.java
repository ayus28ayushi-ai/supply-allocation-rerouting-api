package com.triage.dera.repository;

import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRecordRepository extends JpaRepository<AllocationRecord, Long> {

    List<AllocationRecord> findByRequestedWarIdOrFulfilledWarId(Long requestedWarId, Long fulfilledWarId);

    List<AllocationRecord> findByItem_ItemId(Long itemId);
}
