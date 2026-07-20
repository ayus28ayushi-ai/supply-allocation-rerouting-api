package com.triage.dera.controller;

import com.triage.dera.dto.AllocationRequestDto;
import com.triage.dera.dto.AllocationResponseDto;
import com.triage.dera.service.AllocationRecordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dera")
@AllArgsConstructor
public class AllocationRecordController {
    private AllocationRecordService allocationRecordService;

    @PostMapping("/allocation")
    public ResponseEntity<AllocationResponseDto> createAllocation(@Valid @RequestBody AllocationRequestDto allocationRequestDto){
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(allocationRecordService.createAllocation(allocationRequestDto));

    }
}
