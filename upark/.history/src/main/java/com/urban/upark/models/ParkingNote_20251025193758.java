package com.urban.upark.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParkingNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking_note;

    @Column(nullable = false, precision = 6, scale = 2)
    private Double note;

    @ManyToOne
    @JoinColumn(name = "Id_Users")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "Id_Parking")
    private Parking parking;
}