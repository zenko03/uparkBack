package com.urban.upark.dto;

import lombok.Data;

@Data
public class AnnouncementVehicleDTO {
    private int parkingVehicleId;  // Id du PARKING_VEHICLES à référencer
    private int numbers;            // Nombre de places proposées dans l'annonce
}
