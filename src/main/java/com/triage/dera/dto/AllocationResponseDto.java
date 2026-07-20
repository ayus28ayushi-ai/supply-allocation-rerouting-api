package com.triage.dera.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllocationResponseDto {
    private Long allocationId;
    private String itemName;
    private String requesterName;
    private Integer quantityRequested;
    private String status;
    private LocalDateTime timestamp;

}
