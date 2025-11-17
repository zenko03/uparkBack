package com.urban.upark.dto.parking;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.urban.upark.models.Users;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingResponse {
    private int id_Parking;
    private String label;
    private BigDecimal hourlyRate;
    private String description;
    private String localisation; // Format: "POINT(longitude latitude)"
    private Users user;
}
