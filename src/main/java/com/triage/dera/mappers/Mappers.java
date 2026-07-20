package com.triage.dera.mappers;

import com.triage.dera.dto.AllocationRequestDto;
import com.triage.dera.dto.AllocationResponseDto;
import com.triage.dera.entity.AllocationRecord;
import com.triage.dera.entity.InventoryItem;
import org.springframework.stereotype.Component;

@Component
public class Mappers {

    public AllocationResponseDto mapEntityToDto(AllocationRecord allocResult){
         AllocationResponseDto aRD = new AllocationResponseDto();
         aRD.setAllocationId(allocResult.getAllocationId());
         aRD.setItemName(allocResult.getItem().getItemName());
        aRD.setRequesterName(allocResult.getRequesterName());
        aRD.setQuantityRequested(allocResult.getQuantityRequested());
        aRD.setStatus("SUCCESS!");
        aRD.setTimestamp(allocResult.getTimestamp());

        return aRD;
    }

    public AllocationRecord mapDtoToEntity(AllocationRequestDto allocReqDto, InventoryItem item){
         AllocationRecord allocRec = new AllocationRecord();

         allocRec.setItem(item);
         allocRec.setRequesterName(allocReqDto.getRequesterName());
         allocRec.setQuantityRequested(allocReqDto.getQuantityRequested());

         return  allocRec;
    }
}
