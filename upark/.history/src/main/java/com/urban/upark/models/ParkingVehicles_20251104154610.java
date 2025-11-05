package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Parking_vehicles")
public class ParkingVehicles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking_vehicles;

    @Column(nullable = false)
    private Integer numbers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Vehicles")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicles vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Parking")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Parking parking;
    
    // Relation OneToMany avec AnnouncementsVehicles
    @OneToMany(mappedBy = "parkingVehicles")
    @JsonIgnore
    private List<AnnouncementsVehicles> announcementsVehicles;
}