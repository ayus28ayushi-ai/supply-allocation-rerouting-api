package com.triage.dera.service;

import com.triage.dera.dto.warehousedto.WarehouseCreateRequestDto;
import com.triage.dera.dto.warehousedto.WarehouseResponseDto;
import com.triage.dera.dto.warehousedto.WarehouseUpdateRequestDto;
import com.triage.dera.entity.Warehouse;
import com.triage.dera.exceptions.customexceptions.ResourceAlreadyPresentException;
import com.triage.dera.exceptions.customexceptions.ResourceNotFoundException;
import com.triage.dera.mappers.WarehouseMappers;
import com.triage.dera.repository.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WareHouseRepository wareHouseRepository;
    private final WarehouseMappers mappers;

    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> viewAllWarehouses() {
        List<Warehouse> warehouse = wareHouseRepository.findAll();
        if (warehouse.isEmpty()) {
            throw new ResourceNotFoundException("No warehouses there!");
        }
        return warehouse.stream().map(mappers::mapEntityToWarDto).toList();
    }

    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseCreateRequestDto warehouseCreateRequestDto) {
        Boolean isWarPresent = wareHouseRepository.existsByNameIgnoreCase(warehouseCreateRequestDto.getName());
        if (isWarPresent) {
            throw new ResourceAlreadyPresentException("The warehouse already exists!");
        }

        Warehouse warehouse = mappers.mapWarDtoToEntity(warehouseCreateRequestDto);
        warehouse.setIsActive(true);

        Warehouse savedWare = wareHouseRepository.save(warehouse);

        return mappers.mapEntityToWarDto(savedWare);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDto viewWarehouseById(Long wareId) {
        Optional<Warehouse> ware = wareHouseRepository.findById(wareId);
        if (ware.isEmpty()) {
            throw new ResourceNotFoundException("No warehouse present with id: " + wareId);
        }
        return mappers.mapEntityToWarDto(ware.get());
    }

    @Transactional
    public WarehouseResponseDto updateWarehouseById(Long wareId, WarehouseUpdateRequestDto warehouseUpdateRequestDto) {
        Warehouse war = wareHouseRepository.findById(wareId)
                .orElseThrow(() -> new ResourceNotFoundException("No such warehouse present"));

        //optimistic locking
        if (warehouseUpdateRequestDto.getVersion() != null
                && !warehouseUpdateRequestDto.getVersion().equals(war.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Warehouse.class, wareId);
        }

        if (warehouseUpdateRequestDto.getName() != null) {
            String newName = warehouseUpdateRequestDto.getName();

            //to avoid redundant update
            if (!newName.equalsIgnoreCase(war.getName())) {
                if (wareHouseRepository.existsByNameIgnoreCase(newName)) {
                    throw new ResourceAlreadyPresentException("A warehouse with name '" + newName + "' already exists!");
                }
                war.setName(newName);
            }
        }
            if (warehouseUpdateRequestDto.getLatitude() != null) {
                war.setLatitude(warehouseUpdateRequestDto.getLatitude());
            }
            if (warehouseUpdateRequestDto.getLongitude() != null) {
                war.setLongitude(warehouseUpdateRequestDto.getLongitude());
            }

            //soft deleting the warehouse
            if(warehouseUpdateRequestDto.getIsActive() != null){
                war.setIsActive(true);
            }

            Warehouse savedWare = wareHouseRepository.save(war);
            return mappers.mapEntityToWarDto(savedWare);


    }
}
