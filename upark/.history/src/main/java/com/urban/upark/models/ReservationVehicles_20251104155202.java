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
@Table(name = "Reservation_vehicles")
public class ReservationVehicles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Reservation_vehicles;

    @Column(nullable = false)
    private Integer numbers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Reservation")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Announcements_vehicles")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AnnouncementsVehicles announcementsVehicles;
    
    // Relations OneToMany selon le schéma SQL
    @OneToMany(mappedBy = "reservationVehicles")
    @JsonIgnore
    private List<AvailabilitiesDate> availabilitiesDates;
    
    @OneToMany(mappedBy = "reservationVehicles")
    @JsonIgnore
    private List<AvailabilitiesFrequence> availabilitiesFrequences;
}