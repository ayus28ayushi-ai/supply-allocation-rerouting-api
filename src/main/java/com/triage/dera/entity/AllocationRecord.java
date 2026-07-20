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
    private InventoryItem item;

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

}
