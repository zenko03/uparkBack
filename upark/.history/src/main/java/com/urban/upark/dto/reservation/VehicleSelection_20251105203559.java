package com.urban.upark.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSelection {
    private int vehicleTypeId;  // ID du type de véhicule (1=moto, 2=voiture, 3=utilitaire, etc.)
    private int quantity;       // Nombre sélectionné dans l'interface mobile
}