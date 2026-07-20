package com.triage.dera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Version
    @Column(nullable = false)
    private Long version;


}
