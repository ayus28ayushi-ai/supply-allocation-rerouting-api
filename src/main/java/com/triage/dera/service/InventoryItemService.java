package com.triage.dera.service;

import com.triage.dera.dto.inventoryitemdto.*;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.InventoryRestock;
import com.triage.dera.entity.Warehouse;
import com.triage.dera.exceptions.customexceptions.ResourceNotFoundException;
import com.triage.dera.mappers.InventoryItemMappers;
import com.triage.dera.repository.InventoryItemRepository;
import com.triage.dera.repository.InventoryRestockRepository;
import com.triage.dera.repository.WarehouseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryRestockRepository inventoryRestockRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryItemMappers mappers;

    @Transactional(readOnly = true)
    public List<InventoryItemResponseDto> getAllInventoryForUser() {
        List<InventoryItem> items = inventoryItemRepository.findAll();

        return items.stream()
                .map(mappers::mapEntityToUserResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponseDto> getInventoryByWarehouseForUser(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId);
        }
        List<InventoryItem> items = inventoryItemRepository.findByWarehouse_WarehouseId(warehouseId);

        return items.stream()
                .map(mappers::mapEntityToUserResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponseDto> getWarehousesByItemIdForUser(Long itemId) {
        List<InventoryItem> items = inventoryItemRepository.
                findByItemId(itemId).stream().toList();

        return items.stream()
                .map(mappers::mapEntityToUserResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponseAdminDto> getAllInventoryForAdmin() {
        return inventoryItemRepository.findAll()
                .stream()
                .map(item -> {
                    InventoryRestock latestLog = inventoryRestockRepository
                            .findTopByItemId_ItemIdOrderByIdDesc(item.getItemId())
                            .orElse(null);
                    return mappers.toAdminResponseDto(item, latestLog);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponseAdminDto> getInventoryByWarehouseForAdmin(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId);
        }

        return inventoryItemRepository.findByWarehouse_WarehouseId(warehouseId)
                .stream()
                .map(item -> {
                    InventoryRestock latestLog = inventoryRestockRepository
                            .findTopByItemId_ItemIdOrderByIdDesc(item.getItemId())
                            .orElse(null);
                    return mappers.toAdminResponseDto(item, latestLog);
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public List<InventoryItemResponseAdminDto> getFullItemDetailByItemId(Long itemId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory Item not found with ID: " + itemId));

        List<InventoryRestock> historyLogs = inventoryRestockRepository.findByItemId_ItemIdOrderByIdDesc(itemId);

        if (historyLogs.isEmpty()) {
            return List.of(mappers.toAdminResponseDto(item, null));
        }

        return historyLogs.stream()
                .map(log -> mappers.toAdminResponseDto(item, log))
                .toList();
    }

    @Transactional
    public InventoryItemResponseAdminDto createNewStock(@Valid InventoryItemAdminRequestCreateDto requestDto) {
        Warehouse warehouse = warehouseRepository.findById(requestDto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + requestDto.getWarehouseId()));

        InventoryItem newItem = mappers.mapAdminCreateDtoToEntity(requestDto, warehouse);
        InventoryItem savedItem = inventoryItemRepository.save(newItem);

        InventoryRestock restockLog = mappers.mapAdminCreateDtoToRestockEntity(requestDto, savedItem);
        InventoryRestock savedLog = inventoryRestockRepository.save(restockLog);

        return mappers.toAdminResponseDto(savedItem, savedLog);
    }

    @Transactional
    public InventoryItemResponseAdminDto updateStock(Long itemId, @Valid InventoryItemAdminRequestUpdateDto requestDto) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setQuantityAvailable(item.getQuantityAvailable() + requestDto.getLastQuantityAdded());
        InventoryItem updatedItem = inventoryItemRepository.save(item);

        InventoryRestock restockLog = mappers.mapAdminUpdateDtoToRestockEntity(requestDto, updatedItem);
        InventoryRestock savedLog = inventoryRestockRepository.save(restockLog);

        return mappers.toAdminResponseDto(updatedItem, savedLog);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponseDto> searchItemsByName(String itemName) {
        List<InventoryItem> items = inventoryItemRepository.findByItemNameIgnoreCase(itemName);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No inventory found matching name: " + itemName);
        }

        return items.stream()
                .map(mappers::mapEntityToUserResponseDto)
                .toList();
    }

    @Transactional
    public InventoryItemResponseAdminDto updateItemName(Long itemId, @Valid InventoryItemCatalogUpdateDto requestDto) {

        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory Item not found with ID: " + itemId));

        item.setItemName(requestDto.getItemName().trim());
        InventoryItem updatedItem = inventoryItemRepository.save(item);

        InventoryRestock latestLog = inventoryRestockRepository
                .findTopByItemId_ItemIdOrderByIdDesc(updatedItem.getItemId())
                .orElse(null);

        return mappers.toAdminResponseDto(updatedItem, latestLog);
    }
}
