package com.urban.upark.models;

import jakarta.persistence.*;
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
@Entity
@Table(name = "Commission_received")
public class CommissionReceived {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Commission_received;

    private BigDecimal price;

    @Column(name = "payement_date")
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "Id_Commission_types")
    private CommissionTypes commissionType;

    @ManyToOne
    @JoinColumn(name = "Id_Reservation")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "Id_Payment_status")
    private PaymentStatus paymentStatus;
}