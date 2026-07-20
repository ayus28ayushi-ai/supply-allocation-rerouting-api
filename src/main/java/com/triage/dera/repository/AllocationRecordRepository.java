package com.triage.dera.repository;

import com.triage.dera.entity.AllocationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllocationRecordRepository extends JpaRepository<AllocationRecord, Long> {
}
