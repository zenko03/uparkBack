package com.urban.upark.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceCalculationRequest {
    private int parkingId;
    
    private LocalDateTime startDateTime;
    
    private LocalDateTime endDateTime;
    
    private List<VehicleSelection> selectedVehicles;
}