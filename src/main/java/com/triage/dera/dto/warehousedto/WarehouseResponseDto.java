package com.triage.dera.dto.warehousedto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WarehouseResponseDto {
    private Long warehouseId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
    private Long version;

}
