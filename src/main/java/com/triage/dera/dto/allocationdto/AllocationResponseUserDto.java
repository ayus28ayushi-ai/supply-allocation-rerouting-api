package com.triage.dera.dto.allocationdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllocationResponseUserDto {
    private Long allocationId;
    private String itemName;
    private String requesterName;
    private Integer quantityRequested;
    private String status;
    private LocalDateTime timestamp;
    private String reqWarehouseName;
    private String fulfilledWarehouseName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double distanceKm;


}
