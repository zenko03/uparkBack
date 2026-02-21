package com.urban.upark.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reservation")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Reservation;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "creation_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime creationDate;

    @Column(name = "payement_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime paymentDate;

    @Column(name = "start_datetime", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime endDateTime;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "qr_code_token", unique = true, length = 255)
    private String qrCodeToken;

    @Column(name = "is_validated")
    private Boolean isValidated = false;

    @Column(name = "validated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Reservation_status")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ReservationStatus reservationStatus;
    
    @OneToMany(mappedBy = "reservation")
    @JsonIgnore
    private List<CommissionReceived> commissionsReceived;
    
    @OneToMany(mappedBy = "reservation")
    @JsonIgnore
    private List<ReservationVehicles> reservationVehicles;
}