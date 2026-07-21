package com.triage.dera.dto.allocationdto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AllocationCancelResponseDto {
    private String message;
    private Long allocationId;
    private boolean isActive;
}
