package com.triage.dera.repository;

import com.triage.dera.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WareHouseRepository extends JpaRepository<Warehouse, Long> {

   Boolean existsByNameIgnoreCase(String name);
}
