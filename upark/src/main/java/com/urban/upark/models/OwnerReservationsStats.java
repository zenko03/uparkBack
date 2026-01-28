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
@Table(name = "v_owner_reservations_stats")
public class OwnerReservationsStats {

    @Id
    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "reservations_mois_courant")
    private Long reservationsMoisCourant;

    @Column(name = "reservations_mois_precedent")
    private Long reservationsMoisPrecedent;

    @Column(name = "evolution_pourcentage")
    private BigDecimal evolutionPourcentage;

    @Column(name = "en_attente")
    private Long enAttente;

    @Column(name = "confirmees")
    private Long confirmees;

    @Column(name = "annulees")
    private Long annulees;

    @Column(name = "terminees")
    private Long terminees;
}
