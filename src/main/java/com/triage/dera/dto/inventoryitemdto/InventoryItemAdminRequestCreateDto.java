package com.triage.dera.dto.inventoryitemdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoryItemAdminRequestCreateDto {

    @NotBlank(message = "Item name cannot be blank.")
    private String itemName;
    @NotNull(message = "Warehouse id cannot be null.")
    @Positive
    private Long warehouseId;
    @NotBlank(message = "Name of person creating this is required.")
    private String addedBy;
    @NotNull(message = "Quantity to be added required.")
    private Integer lastQuantityAdded;
}
