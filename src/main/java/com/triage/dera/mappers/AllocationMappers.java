package com.triage.dera.mappers;

import com.triage.dera.dto.allocationdto.AllocationRequestDto;
import com.triage.dera.dto.allocationdto.AllocationResponseDto;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import org.springframework.stereotype.Component;

@Component
public class AllocationMappers {

    public AllocationResponseDto mapEntityToDto(AllocationRecord allocRecord){
         AllocationResponseDto aRD = new AllocationResponseDto();
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

    public AllocationRecord mapDtoToEntity(
            AllocationRequestDto allocReqDto,
            InventoryItem primInvent,
            InventoryItem fulfilledItem,
            Boolean isRerouted,
            Double dist)
    {
         AllocationRecord allocRec = new AllocationRecord();

         allocRec.setItem(fulfilledItem);
        allocRec.setItemName(fulfilledItem.getItemName());
        allocRec.setRequestedWarName(primInvent.getWarehouse().getName());
        allocRec.setRequestedWarId(primInvent.getWarehouse().getWarehouseId());
        allocRec.setFulfilledWarName(fulfilledItem.getWarehouse().getName());
        allocRec.setFulfilledWarId(fulfilledItem.getWarehouse().getWarehouseId());
         allocRec.setRequesterName(allocReqDto.getRequesterName());
         allocRec.setQuantityRequested(allocReqDto.getQuantityRequested());
         allocRec.setDistanceKm(dist);
         allocRec.setIsRerouted(isRerouted);

         return  allocRec;
    }
}
