package com.triage.dera.dto.inventoryitemdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoryItemAdminRequestCreateDto {

    private String itemName;
    private Long warehouseId;
    private String addedBy;
    private Integer lastQuantityAdded;
}
