package com.urban.upark.dto.parking;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingUpdateRequest {
    private String label;
    private BigDecimal hourlyRate;
    private String description;
    private String localisation; // Format: "SRID=4326;POINT(longitude latitude)"
    private Boolean isActive;
    private List<VehicleCountDto> vehicles; // Liste des véhicules avec leurs quantités

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleCountDto {
        private int vehicleId;
        private int count;
    }
}
