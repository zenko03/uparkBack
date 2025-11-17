package com.urban.upark.dto.reservation;

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
public class ReservationDetailDTO {
    
    private int idReservation;
    private BigDecimal totalPrice;
    private LocalDateTime creationDate;
    private LocalDateTime paymentDate;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String paymentMethod;
    
    // User info
    private UserInfo user;
    
    // Parking info
    private ParkingInfo parking;
    
    // Reservation status
    private ReservationStatusInfo reservationStatus;
    
    // Commission
    private BigDecimal commission;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private int idUsers;
        private String firstName;
        private String lastName;
        private String email;
        private String telephone;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParkingInfo {
        private int idParking;
        private String name;
        private String address;
        private int idOwner;
        private String ownerName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationStatusInfo {
        private int idReservationStatus;
        private String label;
        private int value;
    }
}
