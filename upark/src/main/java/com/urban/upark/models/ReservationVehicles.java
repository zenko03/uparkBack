package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Announcements_vehicles")
    private AnnouncementsVehicles announcementsVehicles;
}