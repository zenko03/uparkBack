package com.urban.upark.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateAnnouncementDTO {
    private String description;
    private int parkingId;  // Id du parking à publier
    private boolean published;
    
    // Véhicules acceptés dans cette annonce
    private List<AnnouncementVehicleDTO> vehicles;
    
    // Disponibilités par dates spécifiques (optionnel)
    private List<AvailabilityDateDTO> availabilitiesDates;
    
    // Disponibilités récurrentes par jours (optionnel)
    private List<AvailabilityFrequenceDTO> availabilitiesFrequence;
}
