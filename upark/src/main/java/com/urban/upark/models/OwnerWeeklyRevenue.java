package com.urban.upark.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@IdClass(OwnerWeeklyRevenueId.class)
@Table(name = "v_owner_weekly_revenue")
public class OwnerWeeklyRevenue {

    @Id
    @Column(name = "owner_id")
    private Integer ownerId;

    @Id
    @Column(name = "jour")
    private LocalDate jour;

    @Column(name = "jour_semaine")
    private Integer jourSemaine;

    @Column(name = "revenus_jour")
    private BigDecimal revenusJour;

    @Column(name = "nombre_reservations")
    private Long nombreReservations;

    @Column(name = "revenus_semaine")
    private BigDecimal revenusSemaine;

    @Column(name = "revenus_semaine_precedente")
    private BigDecimal revenusSemainePrecedente;
}
