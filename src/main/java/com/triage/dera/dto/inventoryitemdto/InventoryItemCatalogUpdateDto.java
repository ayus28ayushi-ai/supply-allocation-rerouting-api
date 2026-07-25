package com.triage.dera.dto.inventoryitemdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemCatalogUpdateDto {

    @NotBlank(message = "Item name cannot be blank")
    private String itemName;
}