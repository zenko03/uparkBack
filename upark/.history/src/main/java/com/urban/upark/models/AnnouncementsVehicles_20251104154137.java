package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

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
    
    // Relations OneToMany selon le schéma SQL
    @OneToMany(mappedBy = "announcementsVehicles")
    @JsonIgnore
    private List<AvailabilitiesDate> availabilitiesDates;
    
    @OneToMany(mappedBy = "announcementsVehicles")
    @JsonIgnore
    private List<AvailabilitiesFrequence> availabilitiesFrequences;
    
    @OneToMany(mappedBy = "announcementsVehicles")
    @JsonIgnore
    private List<ReservationVehicles> reservationVehicles;
}