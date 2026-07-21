package com.triage.dera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "allocation_records")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AllocationRecord {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String requesterName;
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    @Column(name = "quantity_claimed" ,nullable = false)
    private Integer quantityRequested;

    //hibernate runs this and updates the time before saving the record in the database
    @PrePersist
    protected void onCreate(){
        this.timestamp = LocalDateTime.now();
    }

    @Column(name = "requested_war_id", nullable = false)
    private Long requestedWarId;
    @Column(nullable = false)
    private String requestedWarName;
    @Column(name = "fulfilled_war_id", nullable = false)
    private Long fulfilledWarId;

    @Column(nullable = false)
    private String fulfilledWarName;
    @Column(name = "is_Rerouted", nullable = false)
    private Boolean isRerouted;
    @Column(name = "distance_km")
    private Double distanceKm;

}
