package com.urban.upark.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ReservationResponse {
    private Integer id;
    private BigDecimal totalPrice;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDateTime;
    
    private String paymentMethod;
    private String status;
    
    // Informations du parking (objet imbriqué)
    private ParkingInfo parking;
    
    // Champs directs pour compatibilité avec le frontend
    private Integer parkingId;
    private String parkingName;
    private String parkingAddress;
    
    // Informations du client (pour le propriétaire)
    private Integer clientId;
    private String clientName;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParkingInfo {
        private Integer id;
        private String name;
        private String address;
        private String city;
        private String zipCode;
    }
}
