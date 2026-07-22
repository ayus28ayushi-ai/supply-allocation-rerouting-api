package com.triage.dera.service;

import com.triage.dera.dto.allocationdto.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    public AllocationResponseUserDto createAllocation (AllocationRequestUserDto allocationRequestUserDto) {

        InventoryItem fulfilledInventory = null;
        boolean isRerouted = false;
        Double minDistanceKm = null;

        Optional<InventoryItem> primInventory = inventoryItemRepository.findByItemNameAndWarehouseWarehouseId(
                allocationRequestUserDto.getItemName(), allocationRequestUserDto.getReqWarehouseId());

        //stock available at the requested warehouse
        Warehouse primWar = null;
        if (primInventory.isPresent() && primInventory.get().getQuantityAvailable() >= allocationRequestUserDto.getQuantityRequested()) {
            fulfilledInventory = primInventory.get();
            isRerouted = false;
            primWar = fulfilledInventory.getWarehouse();
        }
        //rerouting to a different warehouse
        else {
            isRerouted = true;
            List<InventoryItem> secInventoryList = inventoryItemRepository.
                    findByItemNameAndQuantityAvailableGreaterThanEqualAndWarehouseWarehouseIdNot(
                            allocationRequestUserDto.getItemName(),
                            allocationRequestUserDto.getQuantityRequested(),
                            allocationRequestUserDto.getReqWarehouseId()
                    );

            if (secInventoryList.isEmpty()) {
                throw new GlobalStockShortageException("Requested item is out of stock across all the warehouses");
            }

            //if the warehouse don't keep that item, reroute again
            if (primInventory.isPresent()) {
                primWar = primInventory.get().getWarehouse();
            } else {
                primWar = warehouseRepository
                        .findById(allocationRequestUserDto
                                .getReqWarehouseId())
                        .orElseThrow(() -> new WarehouseNotFoundException("Warehouse ID not found"));

            }

            //sorting the potential rerouting candidates so we can keep rerouting if the closest one doesn't work out
            Warehouse finalPrimWar = primWar;
            secInventoryList.sort((i1, i2) -> {
                double d1 = HaversineMathUtility.calcGeoDistance(
                        finalPrimWar.getLatitude(), finalPrimWar.getLongitude(),
                        i1.getWarehouse().getLatitude(), i1.getWarehouse().getLongitude());
                double d2 = HaversineMathUtility.calcGeoDistance(
                        finalPrimWar.getLatitude(), finalPrimWar.getLongitude(),
                        i2.getWarehouse().getLatitude(), i2.getWarehouse().getLongitude());
                return Double.compare(d1, d2);
            });

            // looping and applying the pessimistic lock on the closed candidate
            for (InventoryItem candidate : secInventoryList) {
                Optional<InventoryItem> lockedCandidate = inventoryItemRepository.findByItemId(candidate.getItemId());

                if (lockedCandidate.isPresent() && lockedCandidate.get().getQuantityAvailable() >= allocationRequestUserDto.getQuantityRequested()) {
                    fulfilledInventory = lockedCandidate.get();
                    break;
                }
            }

            if (fulfilledInventory == null) {
                throw new GlobalStockShortageException("Stock was claimed by concurrent orders across all nearby warehouses.");
            }
        }

        minDistanceKm = HaversineMathUtility.calcGeoDistance(
                primWar.getLatitude(),
                primWar.getLongitude(),
                fulfilledInventory.getWarehouse().getLatitude(),
                fulfilledInventory.getWarehouse().getLongitude()
        );

        //updating the stock
        fulfilledInventory.setQuantityAvailable(
                fulfilledInventory.getQuantityAvailable() - allocationRequestUserDto.getQuantityRequested()
        );
        inventoryItemRepository.save(fulfilledInventory);

        //mapping
        AllocationRecord ar = mapper.mapUserDtoToEntity(
                allocationRequestUserDto,
                primWar,
                fulfilledInventory,
                isRerouted,
                minDistanceKm
        );
        AllocationRecord savedRecord = allocationRecordRepository.save(ar);
        return mapper.mapEntityToUserDto(savedRecord);
    }

    public InventoryItem findBestRerouteInventory(Warehouse primWar, List<InventoryItem> secInventItems){
        double minDist = Double.MAX_VALUE;
        InventoryItem bestInvent = null;

        for(InventoryItem item : secInventItems){
            Double dist = HaversineMathUtility.calcGeoDistance(
                    primWar.getLatitude(),
                    primWar.getLongitude(), item.getWarehouse().getLatitude(),
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
    public AllocationResponseUserDto viewAllocation(Long allocationId) {
        AllocationRecord record = allocationRecordRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("No allocation record with id: " + allocationId));

        return mapper.mapEntityToUserDto(record);
    }

    //for viewing all the allocations of a warehouse
    @Transactional(readOnly = true)
    public List<AllocationResponseAdminDto> viewAllAllocationForWarehouse(Long warId) {
       List<AllocationRecord> records = allocationRecordRepository.findByRequestedWarIdOrFulfilledWarId(warId, warId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No Allocation records found for the warehouse id: " + warId);
        }

        return records.stream().map(mapper::mapEntityToAdminDto).toList();
    }

    //for cancelling an allocation and restocking the canceled items
    @Transactional
    public AllocationCancelResponseDto cancelAllocation(Long allocationId, AllocationCancelRequestDto request) {
        AllocationRecord record = allocationRecordRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("No allocation record with id: "+ allocationId));

        //optimistic locking
        if (request.getVersion() != null && !request.getVersion().equals(record.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(AllocationRecord.class, allocationId);
        }

        //checking current status
        if(!record.getIsActive()){
            throw new IllegalStateException("Allocation of id: "+allocationId+" already cancelled.");
        }

        //restocking the inventory
        //pessimistic lock
        InventoryItem item = inventoryItemRepository.findByItemId(record.getItem().getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
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

    @Transactional(readOnly = true)
    public List<AllocationResponseAdminDto> viewAuditHistory() {
        List<AllocationRecord> record = allocationRecordRepository.findAll();
        if(record.isEmpty()){
            throw new ResourceNotFoundException("No history found.");
        }
        return record.stream().map(mapper::mapEntityToAdminDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AllocationResponseAdminDto> viewAllocationByItem(Long itemId) {
        List<AllocationRecord> records = allocationRecordRepository.findByItem_ItemId(itemId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No Allocation records found for the item id: " + itemId);
        }

        return records.stream().map(mapper::mapEntityToAdminDto).toList();
    }
}


