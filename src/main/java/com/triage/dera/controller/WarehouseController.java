package com.triage.dera.controller;

import com.triage.dera.dto.warehousedto.WarehouseCreateRequestDto;
import com.triage.dera.dto.warehousedto.WarehouseResponseDto;
import com.triage.dera.dto.warehousedto.WarehouseUpdateRequestDto;
import com.triage.dera.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dera/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDto>> viewAllWarehouses(){
        return ResponseEntity.ok(warehouseService.viewAllWarehouses());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<WarehouseResponseDto> createWarehouse(@Valid @RequestBody WarehouseCreateRequestDto warehouseCreateRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(warehouseCreateRequestDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{wareId}")
    public ResponseEntity<WarehouseResponseDto> viewWarehouseById(@PathVariable Long wareId){
        return ResponseEntity.status(HttpStatus.OK).body(warehouseService.viewWarehouseById(wareId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{wareId}")
    public ResponseEntity<WarehouseResponseDto> updateWarehouseById(@PathVariable Long wareId, @Valid @RequestBody WarehouseUpdateRequestDto warehouseUpdateRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(warehouseService.updateWarehouseById(wareId,warehouseUpdateRequestDto));
    }

}
