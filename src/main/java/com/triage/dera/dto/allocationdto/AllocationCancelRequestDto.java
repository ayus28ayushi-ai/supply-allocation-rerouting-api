package com.triage.dera.dto.allocationdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllocationCancelRequestDto {
    @NotBlank(message = " Cancellation requester name is required for audit logs")
    private String cancelledBy;

    @NotBlank(message = "Reason for cancellation is required")
    private String reason;
}
