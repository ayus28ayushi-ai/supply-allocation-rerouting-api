package com.triage.dera.service;

import com.triage.dera.dto.AllocationRequestDto;
import com.triage.dera.dto.AllocationResponseDto;
import com.triage.dera.entity.Warehouse;
import com.triage.dera.exceptions.GlobalStockShortageException;
import com.triage.dera.exceptions.WarehouseNotFoundException;
import com.triage.dera.mappers.Mappers;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.repository.AllocationRecordRepository;
import com.triage.dera.repository.InventoryItemRepository;
import com.triage.dera.repository.WareHouseRepository;
import com.triage.dera.utility.HaversineMathUtility;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.min;

@Service
@AllArgsConstructor
public class AllocationRecordService {

    private AllocationRecordRepository allocationRecordRepository;
    private InventoryItemRepository inventoryItemRepository;
    private Mappers mapper;
    private WareHouseRepository warehouseRepository;

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

            fulfilledInventory = findBestRerouteInventory(
                    primInventory.get().getWarehouse(),
                    secInventoryList
                    );
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

}


