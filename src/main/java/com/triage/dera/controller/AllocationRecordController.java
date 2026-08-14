package com.triage.dera.controller;

import com.triage.dera.dto.allocationdto.*;

import com.triage.dera.service.AllocationRecordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/dera")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AllocationRecordController {
    private final AllocationRecordService allocationRecordService;

    //create allocations
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    @PostMapping("/user/allocation")
    public ResponseEntity<AllocationResponseUserDto> createAllocation(@Valid @RequestBody AllocationRequestUserDto allocationRequestUserDto){
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(allocationRecordService.createAllocation(allocationRequestUserDto));

    }

    //fetch details by allocation id
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/allocation/{allocationId}")
    public ResponseEntity<AllocationResponseUserDto> viewAllocationById(@PathVariable Long allocationId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllocation(allocationId));
    }

    //fetch all allocation records for a warehouse
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/allocation/war/{warId}")
    public ResponseEntity<List<AllocationResponseAdminDto>> viewAllAllocationForWarehouse(@PathVariable Long warId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllAllocationForWarehouse(warId));
    }

    //fetch details of an item from the allocation history
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/allocation/item/{itemId}")
    public ResponseEntity<List<AllocationResponseAdminDto>> viewAllocationByItemId(@PathVariable Long itemId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAllocationByItem(itemId));
    }

    //fetch the whole allocation log for the admin purpose
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/allocation")
    public ResponseEntity<Page<AllocationResponseAdminDto>> viewAuditHistory(
             @ParameterObject Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(allocationRecordService.viewAuditHistory(pageable));
    }

    //cancel allocation and restock the canceled items
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    @PatchMapping("/user/allocation/{allocationId}/cancel")
    public ResponseEntity<AllocationCancelResponseDto> cancelAllocation(@PathVariable Long allocationId, @Valid @RequestBody AllocationCancelRequestDto request){
        return ResponseEntity.ok(allocationRecordService.cancelAllocation(allocationId, request));
    }

}
