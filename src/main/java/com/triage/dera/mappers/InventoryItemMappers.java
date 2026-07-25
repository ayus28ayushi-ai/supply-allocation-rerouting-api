package com.triage.dera.mappers;

import com.triage.dera.dto.inventoryitemdto.InventoryItemAdminRequestCreateDto;
import com.triage.dera.dto.inventoryitemdto.InventoryItemAdminRequestUpdateDto;
import com.triage.dera.dto.inventoryitemdto.InventoryItemResponseAdminDto;
import com.triage.dera.dto.inventoryitemdto.InventoryItemResponseDto;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.InventoryRestock;
import com.triage.dera.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class InventoryItemMappers {

    public InventoryItemResponseDto mapEntityToUserResponseDto(InventoryItem item) {
        if (item == null) {
            return null;
        }

        return InventoryItemResponseDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .warehouse(item.getWarehouse())
                .quantityAvailable(item.getQuantityAvailable())
                .version(item.getVersion())
                .build();
    }

    public InventoryItem mapAdminCreateDtoToEntity(InventoryItemAdminRequestCreateDto requestDto, Warehouse warehouse) {
        if (requestDto == null) {
            return null;
        }

        return InventoryItem.builder()
                .warehouse(warehouse)
                .itemName(requestDto.getItemName())
                .quantityAvailable(requestDto.getLastQuantityAdded())
                .build();
    }

    public InventoryRestock mapAdminCreateDtoToRestockEntity(InventoryItemAdminRequestCreateDto requestDto, InventoryItem item) {
        if (requestDto == null || item == null) {
            return null;
        }

        return InventoryRestock.builder()
                .itemId(item)
                .itemName(item.getItemName())
                .warehouseId(item.getWarehouse().getWarehouseId())
                .warehouseName(item.getWarehouse().getName())
                .lastQuantityAdded(requestDto.getLastQuantityAdded())
                .addedBy(requestDto.getAddedBy())
                .addedAt(Timestamp.from(Instant.now()))
                .build();
    }

    public InventoryRestock mapAdminUpdateDtoToRestockEntity(InventoryItemAdminRequestUpdateDto updateDto, InventoryItem item) {
        if (updateDto == null || item == null) {
            return null;
        }

        Long warehouseId = (item.getWarehouse() != null) ? item.getWarehouse().getWarehouseId() : null;
        String warehouseName = (item.getWarehouse() != null) ? item.getWarehouse().getName() : null;

        return InventoryRestock.builder()
                .itemId(item)
                .itemName(item.getItemName())
                .warehouseId(warehouseId)
                .warehouseName(warehouseName)
                .lastQuantityAdded(updateDto.getLastQuantityAdded())
                .addedBy(updateDto.getAddedBy())
                .addedAt(Timestamp.from(Instant.now()))
                .build();
    }

    public InventoryItemResponseAdminDto toAdminResponseDto(InventoryItem item, InventoryRestock restockLog) {
        if (item == null) {
            return null;
        }

        InventoryItemResponseAdminDto.InventoryItemResponseAdminDtoBuilder builder = InventoryItemResponseAdminDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .quantityAvailable(item.getQuantityAvailable());


        if (item.getWarehouse() != null) {
            builder.warehouseId(item.getWarehouse().getWarehouseId())
                    .warehouseName(item.getWarehouse().getName());
        }


        if (restockLog != null) {
            builder.restockId(restockLog.getId())
                    .lastQuantityAdded(restockLog.getLastQuantityAdded())
                    .addedBy(restockLog.getAddedBy())
                    .addedAt(restockLog.getAddedAt());
        }

        return builder.build();
    }
}
