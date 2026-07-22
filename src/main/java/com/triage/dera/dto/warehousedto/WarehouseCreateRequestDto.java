package com.triage.dera.dto.warehousedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WarehouseCreateRequestDto {
    @NotBlank(message = "Warehouse name can't be empty")
    private String name;
    @NotNull(message = "Latitude can't be null")
    private Double latitude;
    @NotNull(message = "Longitude can't be null")
    private Double longitude;
}
