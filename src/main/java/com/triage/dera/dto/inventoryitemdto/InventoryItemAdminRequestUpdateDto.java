package com.triage.dera.dto.inventoryitemdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data@Builder
public class InventoryItemAdminRequestUpdateDto {
    private String addedBy;
    private Integer lastQuantityAdded;
}
