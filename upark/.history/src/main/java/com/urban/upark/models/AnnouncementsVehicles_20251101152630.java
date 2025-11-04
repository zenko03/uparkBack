package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Announcements_vehicles")
public class AnnouncementsVehicles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Announcements_vehicles;

    @Column(nullable = false)
    private Integer numbers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Announcements")
    private Announcements announcements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Parking_vehicles")
    private ParkingVehicles parkingVehicles;
    
    @OneToMany(mappedBy = "announcementsVehicles")
    private List<AvailabilitiesDate> availabilitiesDates;
}