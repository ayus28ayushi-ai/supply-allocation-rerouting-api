package com.triage.dera.service;

import com.triage.dera.dto.AllocationRequestDto;
import com.triage.dera.dto.AllocationResponseDto;
import com.triage.dera.exceptions.InsufficientStockException;
import com.triage.dera.exceptions.ResourceNotFoundException;
import com.triage.dera.mappers.Mappers;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.repository.AllocationRecordRepository;
import com.triage.dera.repository.InventoryItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AllocationRecordService {

    private AllocationRecordRepository allocationRecordRepository;
    private InventoryItemRepository inventoryItemRepository;
    private Mappers mapper;

    @Transactional
    public AllocationResponseDto createAllocation (AllocationRequestDto allocationRequestDto){
        InventoryItem item = inventoryItemRepository
                .findByItemNameAndWarehouseWarehouseId(
                        allocationRequestDto.getItemName(), allocationRequestDto.getWarehouseId()
                ).orElseThrow(() -> new ResourceNotFoundException(
                        "Item "+ allocationRequestDto.getItemName() + "not available at "+ allocationRequestDto.getWarehouseId()
                ));

        //checking for stock availability
        if(item.getQuantityAvailable() < allocationRequestDto.getQuantityRequested()){
            throw new InsufficientStockException("Item out of stock at this warehouse");
        }
        //updating the stock quantity at the warehouse
        item.setQuantityAvailable(item.getQuantityAvailable() - allocationRequestDto.getQuantityRequested());
        inventoryItemRepository.save(item);

        //mapping
        AllocationRecord ar = mapper.mapDtoToEntity(allocationRequestDto, item);
        AllocationRecord savedRecord = allocationRecordRepository.save(ar);
        return mapper.mapEntityToDto(savedRecord);
    }

}
