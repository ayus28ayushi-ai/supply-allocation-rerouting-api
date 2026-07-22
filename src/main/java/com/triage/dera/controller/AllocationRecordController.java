package com.triage.dera.controller;

import com.triage.dera.dto.allocationdto.*;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.service.AllocationRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dera/allocation")
@RequiredArgsConstructor
public class AllocationRecordController {
    private final AllocationRecordService allocationRecordService;

    //create allocations
    @PostMapping
    public ResponseEntity<AllocationResponseUserDto> createAllocation(@Valid @RequestBody AllocationRequestUserDto allocationRequestUserDto){
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(allocationRecordService.createAllocation(allocationRequestUserDto));

    }

    //fetch details by allocation id
    @GetMapping("/{allocationId}")
    public ResponseEntity<AllocationResponseUserDto> viewAllocationById(@PathVariable Long allocationId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllocation(allocationId));
    }

    //fetch all allocation records for a warehouse
    @GetMapping("/war/{warId}")
    public ResponseEntity<List<AllocationResponseAdminDto>> viewAllAllocationForWarehouse(@PathVariable Long warId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllAllocationForWarehouse(warId));
    }

    //fetch details of an item from the allocation history
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<AllocationResponseAdminDto>> viewAllocationByItemId(@PathVariable Long itemId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllocationByItem(itemId));
    }

    //fetch the whole allocation log for the admin purpose
    @GetMapping
    public ResponseEntity<List<AllocationResponseAdminDto>> viewAuditHistory(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAuditHistory());
    }

    //cancel allocation and restock the canceled items
    @PatchMapping("/{allocationId}/cancel")
    public ResponseEntity<AllocationCancelResponseDto> cancelAllocation(@PathVariable Long allocationId, @Valid @RequestBody AllocationCancelRequestDto request){
        return ResponseEntity.ok(allocationRecordService.cancelAllocation(allocationId, request));
    }

}
