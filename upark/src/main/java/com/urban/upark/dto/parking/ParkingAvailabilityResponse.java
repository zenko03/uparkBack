package com.urban.upark.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAvailabilityResponse {
    private Integer parkingId;
    private String parkingName;
    private String description;
    private String availabilitySchedule; // Ex: "Du lundi au vendredi à 10:30-18:30"
    private List<VehicleAvailability> vehicleAvailabilities;
}
