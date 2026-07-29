package com.triage.dera.controller;

import com.triage.dera.dto.inventoryitemdto.*;
import com.triage.dera.service.InventoryItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dera/stock")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    //ENDPOINTS FOR THE USER
    //get all inventory item details
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<InventoryItemResponseDto>> getAllInventoryForUser() {
        return ResponseEntity.ok(inventoryItemService.getAllInventoryForUser());
    }

    //get all inventory item details by warehouse id
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByWarehouseForUser(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryItemService.getInventoryByWarehouseForUser(warehouseId));
    }

    //get all inventory item details by item id
    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<InventoryItemResponseDto>> getWarehousesByItemIdForUser(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.getWarehousesByItemIdForUser(itemId));
    }

    //ENDPOINTS FOR THE ADMIN
    //get the latest log of  inventory item (audit history)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryItemResponseAdminDto>> getAllInventoryForAdmin(
            @PageableDefault(
                    page = 0, size = 6, sort = "itemId", direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryItemService.getAllInventoryForAdmin(pageable));
    }

    //get all inventory item details by warehouse id
    @GetMapping("/admin/warehouse/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryItemResponseAdminDto>> getInventoryByWarehouseForAdmin(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryItemService.getInventoryByWarehouseForAdmin(warehouseId));
    }

    //get all inventory item details by item id
    @GetMapping("/admin/item/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryItemResponseAdminDto>> getFullItemDetailByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.getFullItemDetailByItemId(itemId));
    }

    //create new items
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItemResponseAdminDto> createNewStock(@Valid @RequestBody InventoryItemAdminRequestCreateDto requestDto) {
        InventoryItemResponseAdminDto response = inventoryItemService.createNewStock(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //update items
    @PatchMapping("/admin/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItemResponseAdminDto> updateStock(@PathVariable Long itemId, @Valid @RequestBody InventoryItemAdminRequestUpdateDto requestDto) {
        return ResponseEntity.ok(inventoryItemService.updateStock(itemId, requestDto));
    }

    //search by name
    @GetMapping("/item/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryItemResponseDto>> searchItemsByName(
            @RequestParam String name) {

        List<InventoryItemResponseDto> response = inventoryItemService.searchItemsByName(name);
        return ResponseEntity.ok(response);
    }

    //change item name
    @PutMapping("/admin/item/{itemId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItemResponseAdminDto> updateItemName(
            @PathVariable Long itemId,
            @Valid @RequestBody InventoryItemCatalogUpdateDto requestDto) {

        InventoryItemResponseAdminDto response = inventoryItemService.updateItemName(itemId, requestDto);
        return ResponseEntity.ok(response);
    }

}