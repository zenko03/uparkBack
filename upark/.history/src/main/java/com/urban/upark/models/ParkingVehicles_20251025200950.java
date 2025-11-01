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
public class ParkingVehicles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking_vehicles;

    @Column(nullable = false)
    private Integer numbers;

    @ManyToOne
    @JoinColumn(name = "Id_Vehicles")
    private Vehicles vehicle;

    @ManyToOne
    @JoinColumn(name = "Id_Parking")
    private Parking parking;
}