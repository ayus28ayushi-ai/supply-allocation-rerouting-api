package com.triage.dera.mappers;

import com.triage.dera.dto.warehousedto.WarehouseCreateRequestDto;
import com.triage.dera.dto.warehousedto.WarehouseResponseDto;
import com.triage.dera.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMappers {

    public WarehouseResponseDto mapEntityToWarDto(Warehouse warehouse){
        if(warehouse == null) return null;

        return WarehouseResponseDto.builder()
                .warehouseId(warehouse.getWarehouseId())
                .name(warehouse.getName())
                .latitude(warehouse.getLatitude())
                .longitude(warehouse.getLongitude())
                .isActive(warehouse.getIsActive())
                .version(warehouse.getVersion())
                .build();
    }

    public Warehouse mapWarDtoToEntity (WarehouseCreateRequestDto warehouseCreateRequestDto){
        return Warehouse.builder()
                .name(warehouseCreateRequestDto.getName())
                .latitude(warehouseCreateRequestDto.getLatitude())
                .longitude(warehouseCreateRequestDto.getLongitude())
                .build();
    }
}
