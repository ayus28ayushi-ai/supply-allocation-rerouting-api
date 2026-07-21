package com.triage.dera.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllocationRequestDto {

    @NotBlank(message = "Item name must not be blank/empty/null.")
    private String itemName;
    @NotNull(message = "Warehouse id must not be null/empty.")
    private Long reqWarehouseId;
    @NotBlank(message = "Requester name must not be blank/empty/null.")
    private String requesterName;
    @NotNull(message = "Quantity requested should be a positive number")
    @Min(1)
    private Integer quantityRequested;

}
