package com.triage.dera.dto.inventoryitemdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data@Builder
public class InventoryItemAdminRequestUpdateDto {
    @NotBlank(message = "Name of person updating required")
    private String addedBy;
    @NotNull(message = "Quantity to be incremented required.")
    private Integer lastQuantityAdded;
}
