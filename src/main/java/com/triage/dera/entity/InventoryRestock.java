package com.triage.dera.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "inventory_restock")
@NoArgsConstructor
@AllArgsConstructor
@Data@Builder
public class InventoryRestock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private InventoryItem itemId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private String warehouseName;

    @Column(nullable = false)
    private Integer lastQuantityAdded;

    @Column(nullable = false)
    private String addedBy;

    @Column(nullable = false)
    private Timestamp addedAt;


}
