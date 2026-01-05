package com.urban.upark.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class AvailabilityFrequenceDTO {
    private int dayOfWeekId;  // 1=Lundi, 2=Mardi, etc.
    private LocalTime startHour;
    private LocalTime endHour;
}
