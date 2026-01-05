package com.urban.upark.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Table de liaison entre ReservationRequest et AnnouncementsVehicles
 * Stocke les types de véhicules sélectionnés lors de la création d'une demande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_request_vehicles")
public class ReservationRequestVehicles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_request_vehicles")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation_request", nullable = false)
    @JsonIgnoreProperties({"selectedVehicles", "announcement", "requester"})
    private ReservationRequest reservationRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_announcements_vehicles", nullable = false)
    @JsonIgnoreProperties({"announcements", "parkingVehicles", "reservationVehicles"})
    private AnnouncementsVehicles announcementsVehicles;

    @Column(name = "numbers", nullable = false)
    private Integer numbers; // Quantité sélectionnée
}
