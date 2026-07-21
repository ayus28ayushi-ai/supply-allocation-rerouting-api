package com.triage.dera.mappers;

import com.triage.dera.dto.AllocationRequestDto;
import com.triage.dera.dto.AllocationResponseDto;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import com.triage.dera.entity.Warehouse;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Component;

@Component
public class Mappers {

    public AllocationResponseDto mapEntityToDto(AllocationRecord allocResult){
         AllocationResponseDto aRD = new AllocationResponseDto();
         aRD.setAllocationId(allocResult.getAllocationId());
         aRD.setItemName(allocResult.getItemId().getItemName());
        aRD.setRequesterName(allocResult.getRequesterName());
        aRD.setQuantityRequested(allocResult.getQuantityRequested());
        if(Boolean.TRUE.equals(allocResult.getIsRerouted())){
            aRD.setDistanceKm(allocResult.getDistanceKm());
            aRD.setStatus(" NEAREST WAREHOUSE REROUTING SUCCESSFUL!");
        }
        else{
            aRD.setStatus("SUCCESS!");
        }
        aRD.setReqWarehouseName(allocResult.getRequestedWarName());
        aRD.setFulfilledWarehouseName(allocResult.getFulfilledWarName());
        aRD.setTimestamp(allocResult.getTimestamp());

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

         allocRec.setItemId(fulfilledItem);
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
