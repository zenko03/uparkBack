package com.urban.upark.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAvailability {
    private Integer vehicleTypeId;
    private String vehicleType;
    private String vehicleIcon;
    private Integer totalCapacity;
    private Integer availableCapacity;
    private Boolean isAvailable;
}
