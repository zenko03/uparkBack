package com.urban.upark.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "Parking_note")
public class ParkingNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking_note;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Users")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Parking")
    private Parking parking;
}