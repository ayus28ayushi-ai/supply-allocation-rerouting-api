package com.triage.dera.dto.inventoryitemdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data@Builder
public class InventoryItemResponseAdminDto {
    private Long restockId;
    private Long itemId;
    private String itemName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantityAvailable;
    private Integer lastQuantityAdded;
    private String addedBy;
    private Timestamp addedAt;

}
