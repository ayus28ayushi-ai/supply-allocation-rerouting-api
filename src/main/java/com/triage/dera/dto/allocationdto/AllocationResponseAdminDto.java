package com.triage.dera.dto.allocationdto;

import com.triage.dera.entity.InventoryItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AllocationResponseAdminDto {

    private Long allocationId;
    private InventoryItem item;
    private String itemName;
    private String requesterName;
    private LocalDateTime timestamp;
    private Integer quantityRequested;
    protected void onCreate(){
        this.timestamp = LocalDateTime.now();
    }

    private Long requestedWarId;
    private String requestedWarName;
    private Long fulfilledWarId;
    private String fulfilledWarName;
    private Boolean isRerouted;
    private Double distanceKm;
    private Boolean isActive = true;
    private String cancelledBy;
}
