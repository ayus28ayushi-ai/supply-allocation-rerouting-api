package com.triage.dera.mappers;

import com.triage.dera.dto.allocationdto.AllocationRequestUserDto;
import com.triage.dera.dto.allocationdto.AllocationResponseAdminDto;
import com.triage.dera.dto.allocationdto.AllocationResponseUserDto;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class AllocationMappers {

    public AllocationResponseUserDto mapEntityToUserDto(AllocationRecord allocRecord){
         AllocationResponseUserDto aRD = new AllocationResponseUserDto();
         aRD.setAllocationId(allocRecord.getAllocationId());
         aRD.setItemName(allocRecord.getItem().getItemName());
        aRD.setRequesterName(allocRecord.getRequesterName());
        aRD.setQuantityRequested(allocRecord.getQuantityRequested());
        if(Boolean.TRUE.equals(allocRecord.getIsRerouted())){
            aRD.setDistanceKm(allocRecord.getDistanceKm());
            aRD.setStatus(" NEAREST WAREHOUSE REROUTING SUCCESSFUL!");
        }
        else{
            aRD.setStatus("SUCCESS!");
        }
        aRD.setReqWarehouseName(allocRecord.getRequestedWarName());
        aRD.setFulfilledWarehouseName(allocRecord.getFulfilledWarName());
        aRD.setTimestamp(allocRecord.getTimestamp());

        return aRD;
    }

    public AllocationRecord mapUserDtoToEntity(
            AllocationRequestUserDto allocReqDto,
            Warehouse primWar,
            InventoryItem fulfilledItem,
            Boolean isRerouted,
            Double dist)
    {
         AllocationRecord allocRec = new AllocationRecord();

         allocRec.setItem(fulfilledItem);
        allocRec.setItemName(fulfilledItem.getItemName());
        allocRec.setRequestedWarName(primWar.getName());
        allocRec.setRequestedWarId(primWar.getWarehouseId());
        allocRec.setFulfilledWarName(fulfilledItem.getWarehouse().getName());
        allocRec.setFulfilledWarId(fulfilledItem.getWarehouse().getWarehouseId());
         allocRec.setRequesterName(allocReqDto.getRequesterName());
         allocRec.setQuantityRequested(allocReqDto.getQuantityRequested());
         allocRec.setDistanceKm(dist);
         allocRec.setIsRerouted(isRerouted);

         return  allocRec;
    }

    public AllocationResponseAdminDto mapEntityToAdminDto(AllocationRecord allocRec){
        if (allocRec == null) {
            return null;
        }

        return AllocationResponseAdminDto.builder()
                .allocationId(allocRec.getAllocationId())
                .item(allocRec.getItem())
                .itemName(allocRec.getItemName())
                .requesterName(allocRec.getRequesterName())
                .timestamp(allocRec.getTimestamp())
                .quantityRequested(allocRec.getQuantityRequested())
                .requestedWarId(allocRec.getRequestedWarId())
                .requestedWarName(allocRec.getRequestedWarName())
                .fulfilledWarId(allocRec.getFulfilledWarId())
                .fulfilledWarName(allocRec.getFulfilledWarName())
                .isRerouted(allocRec.getIsRerouted())
                .distanceKm(allocRec.getDistanceKm())
                .isActive(allocRec.getIsActive())
                .cancelledBy(allocRec.getCancelledBy())
                .build();
    }
}
