package com.triage.dera.service;

import com.triage.dera.dto.allocationdto.AllocationCancelRequestDto;
import com.triage.dera.dto.allocationdto.AllocationCancelResponseDto;
import com.triage.dera.dto.allocationdto.AllocationRequestDto;
import com.triage.dera.dto.allocationdto.AllocationResponseDto;
import com.triage.dera.entity.Warehouse;
import com.triage.dera.exceptions.customexceptions.GlobalStockShortageException;
import com.triage.dera.exceptions.customexceptions.ResourceNotFoundException;
import com.triage.dera.exceptions.customexceptions.WarehouseNotFoundException;
import com.triage.dera.mappers.AllocationMappers;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.repository.AllocationRecordRepository;
import com.triage.dera.repository.InventoryItemRepository;
import com.triage.dera.repository.WareHouseRepository;
import com.triage.dera.utility.HaversineMathUtility;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AllocationRecordService {

    private final AllocationRecordRepository allocationRecordRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AllocationMappers mapper;
    private final WareHouseRepository warehouseRepository;

    @Transactional
    public AllocationResponseDto createAllocation (AllocationRequestDto allocationRequestDto){

        InventoryItem fulfilledInventory = null;
        boolean isRerouted = false;
        Double minDistanceKm = null;

        Optional<InventoryItem> primInventory = inventoryItemRepository.findByItemNameAndWarehouseWarehouseId(
                allocationRequestDto.getItemName(), allocationRequestDto.getReqWarehouseId());

        //stock available at the requested warehouse
        if(primInventory.isPresent() && primInventory.get().getQuantityAvailable() >= allocationRequestDto.getQuantityRequested()){
            fulfilledInventory = primInventory.get();
            isRerouted = false;
        }
        //rerouting to a different warehouse
        else{
            isRerouted = true;
            List<InventoryItem> secInventoryList = inventoryItemRepository.
                    findByItemNameAndQuantityAvailableGreaterThanEqualAndWarehouseWarehouseIdNot(
                            allocationRequestDto.getItemName(),
                            allocationRequestDto.getQuantityRequested(),
                            allocationRequestDto.getReqWarehouseId()
                    );

            if(secInventoryList.isEmpty()){
                throw new GlobalStockShortageException("Requested item is out of stock across all the warehouses");
            }

            //if the warehouse don't keep that item, reroute again
            Warehouse primWar;
            if(primInventory.isPresent()){
                primWar = primInventory.get().getWarehouse();
            } else{
                primWar = warehouseRepository
                        .findById(allocationRequestDto
                                .getReqWarehouseId())
                        .orElseThrow(() -> new WarehouseNotFoundException("Warehouse ID not found"));

            }

            fulfilledInventory = findBestRerouteInventory(primWar, secInventoryList);
        }
        minDistanceKm = HaversineMathUtility.calcGeoDistance(
                primInventory.get().getWarehouse().getLatitude(),
                primInventory.get().getWarehouse().getLongitude(),
                fulfilledInventory.getWarehouse().getLatitude(),
                fulfilledInventory.getWarehouse().getLongitude()
        );

        //updating the stock
        fulfilledInventory.setQuantityAvailable(
                fulfilledInventory.getQuantityAvailable() - allocationRequestDto.getQuantityRequested()
        );
        inventoryItemRepository.save(fulfilledInventory);

        //mapping
        AllocationRecord ar = mapper.mapDtoToEntity(allocationRequestDto, primInventory.get(), fulfilledInventory,isRerouted, minDistanceKm);
        AllocationRecord savedRecord = allocationRecordRepository.save(ar);
        return mapper.mapEntityToDto(savedRecord);
    }

    public InventoryItem findBestRerouteInventory(Warehouse primWar, List<InventoryItem> secInventItems){
        double minDist = Double.MAX_VALUE;
        InventoryItem bestInvent = null;

        for(InventoryItem item : secInventItems){
            Double dist = HaversineMathUtility.calcGeoDistance(
                    primWar.getLatitude(),
                    primWar.getLatitude(), item.getWarehouse().getLatitude(),
                    item.getWarehouse().getLongitude()
            );
            if(dist < minDist){
                minDist = dist;
                bestInvent = item;

            }
        }
        return bestInvent;
    }

    //for viewing allocation record by id
    @Transactional(readOnly = true)
    public AllocationResponseDto viewAllocation(Long allocationId) {
        AllocationRecord record = allocationRecordRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("No allocation record with id: " + allocationId));

        return mapper.mapEntityToDto(record);
    }

    //for viewing all the allocations of a warehouse
    @Transactional(readOnly = true)
    public List<AllocationResponseDto> viewAllAllocationForWarehouse(Long warId) {
       List<AllocationRecord> records = allocationRecordRepository.findByRequestedWarIdOrFulfilledWarId(warId, warId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No Allocation records found for the warehouse id: " + warId);
        }

        return records.stream().map(mapper::mapEntityToDto).toList();
    }

    //for cancelling an allocation and restocking the canceled items
    public AllocationCancelResponseDto cancelAllocation(Long allocationId, AllocationCancelRequestDto request) {
        AllocationRecord record = allocationRecordRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("No allocation record with id: "+ allocationId));

        //checking current status
        if(!record.getIsActive()){
            throw new IllegalStateException("Allocation of id: "+allocationId+" already cancelled.");
        }

        //restocking the inventory
        InventoryItem item = record.getItem();
        item.setQuantityAvailable(record.getQuantityRequested() + item.getQuantityAvailable());
        inventoryItemRepository.save(item);

        //updating records
        record.setIsActive(false);
        record.setCancelledBy(request.getCancelledBy());
        allocationRecordRepository.save(record);

        //message for the user
        String message = String.format(
                "Allocation id:%d for '%s' was cancelled by %s. %d units restored to warehouse stock.",
                allocationId,
                item.getItemName(),
               record.getCancelledBy(),
                record.getQuantityRequested()
        );
        return AllocationCancelResponseDto.builder()
                .message(message)
                .allocationId(allocationId)
                .isActive(false)
                .build();

    }
}


