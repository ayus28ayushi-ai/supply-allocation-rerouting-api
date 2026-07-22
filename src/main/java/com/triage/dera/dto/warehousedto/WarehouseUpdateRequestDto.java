package com.triage.dera.dto.warehousedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WarehouseUpdateRequestDto {
    private String name;
    private Double Latitude;
    private Double Longitude;
    private Long version;
    private Boolean isActive;

}
