package com.urban.upark.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "v_owner_dashboard_summary")
public class OwnerDashboardSummary {

    @Id
    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "revenus_mois_courant")
    private BigDecimal revenusMoisCourant;

    @Column(name = "revenus_mois_precedent")
    private BigDecimal revenusMoisPrecedent;

    @Column(name = "evolution_revenus")
    private BigDecimal evolutionRevenus;

    @Column(name = "reservations_mois_courant")
    private Long reservationsMoisCourant;

    @Column(name = "reservations_mois_precedent")
    private Long reservationsMoisPrecedent;

    @Column(name = "evolution_reservations")
    private BigDecimal evolutionReservations;

    @Column(name = "nombre_parkings")
    private Long nombreParkings;

    @Column(name = "capacite_totale")
    private Long capaciteTotale;

    @Column(name = "places_disponibles")
    private Long placesDisponibles;

    @Column(name = "places_occupees")
    private Long placesOccupees;

    @Column(name = "taux_occupation")
    private BigDecimal tauxOccupation;

    @Column(name = "notifications_non_lues")
    private Long notificationsNonLues;
}
