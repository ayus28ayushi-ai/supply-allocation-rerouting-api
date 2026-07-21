package com.triage.dera.controller;

import com.triage.dera.dto.allocationdto.AllocationCancelRequestDto;
import com.triage.dera.dto.allocationdto.AllocationCancelResponseDto;
import com.triage.dera.dto.allocationdto.AllocationRequestDto;
import com.triage.dera.dto.allocationdto.AllocationResponseDto;
import com.triage.dera.entity.Warehouse;
import com.triage.dera.service.AllocationRecordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    public ResponseEntity<AllocationResponseDto> createAllocation(@Valid @RequestBody AllocationRequestDto allocationRequestDto){
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(allocationRecordService.createAllocation(allocationRequestDto));

    }

    //fetch details by allocation id
    @GetMapping("/{allocationId}")
    public ResponseEntity<AllocationResponseDto> viewAllocationById(@PathVariable Long allocationId){
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(allocationRecordService.viewAllocation(allocationId));
    }

    //fetch all allocation records for a warehouse
    @GetMapping("/war/{warId}")
    public ResponseEntity<List<AllocationResponseDto>> viewAllAllocationForWarehouse(@PathVariable Long warId){
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(allocationRecordService.viewAllAllocationForWarehouse(warId));
    }

    //cancel allocation and restock the canceled items
    @PatchMapping("/{allocationId}/cancel")
    public ResponseEntity<AllocationCancelResponseDto> cancelAllocation(@PathVariable Long allocationId, @Valid @RequestBody AllocationCancelRequestDto request){
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(allocationRecordService.cancelAllocation(allocationId, request));
    }

}
