package com.triage.dera.controller;

import com.triage.dera.dto.inventoryitemdto.*;
import com.triage.dera.service.InventoryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
   public ResponseEntity<List<InventoryItemResponseDto>> getAllInventoryForUser() {
       return ResponseEntity.ok(inventoryItemService.getAllInventoryForUser());
   }

   //get all inventory item details by warehouse id
    @GetMapping("warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByWarehouseForUser(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryItemService.getInventoryByWarehouseForUser(warehouseId));
    }

    //get all inventory item details by item id
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<InventoryItemResponseDto>> getWarehousesByItemIdForUser(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.getWarehousesByItemIdForUser(itemId));
    }

    //ENDPOINTS FOR THE ADMIN
    //get the latest log of  inventory item
    @GetMapping("/admin")
    public ResponseEntity<List<InventoryItemResponseAdminDto>> getAllInventoryForAdmin() {
        return ResponseEntity.ok(inventoryItemService.getAllInventoryForAdmin());
    }

    //get all inventory item details by warehouse id
    @GetMapping("/admin/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemResponseAdminDto>> getInventoryByWarehouseForAdmin(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryItemService.getInventoryByWarehouseForAdmin(warehouseId));
    }

    //get all inventory item details by item id
    @GetMapping("/admin/item/{itemId}")
    public ResponseEntity<List<InventoryItemResponseAdminDto>> getFullItemDetailByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.getFullItemDetailByItemId(itemId));
    }

    //create new items
    @PostMapping("/admin")
    public ResponseEntity<InventoryItemResponseAdminDto> createNewStock(@Valid @RequestBody InventoryItemAdminRequestCreateDto requestDto) {
        InventoryItemResponseAdminDto response = inventoryItemService.createNewStock(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //update items
    @PatchMapping("/admin/{itemId}")
    public ResponseEntity<InventoryItemResponseAdminDto> updateStock(@PathVariable Long itemId, @Valid @RequestBody InventoryItemAdminRequestUpdateDto requestDto) {
        return ResponseEntity.ok(inventoryItemService.updateStock(itemId, requestDto));
    }

    //search by name
    @GetMapping("/item/search")
    public ResponseEntity<List<InventoryItemResponseDto>> searchItemsByName(
            @RequestParam String name) {

        List<InventoryItemResponseDto> response = inventoryItemService.searchItemsByName(name);
        return ResponseEntity.ok(response);
    }

    //change item name
    @PutMapping("/admin/item/{itemId}/details")
    public ResponseEntity<InventoryItemResponseAdminDto> updateItemName(
            @PathVariable Long itemId,
            @Valid @RequestBody InventoryItemCatalogUpdateDto requestDto) {

        InventoryItemResponseAdminDto response = inventoryItemService.updateItemName(itemId, requestDto);
        return ResponseEntity.ok(response);
    }

}
