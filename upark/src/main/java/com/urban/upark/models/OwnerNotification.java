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
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "v_owner_notifications")
public class OwnerNotification {

    @Id
    @Column(name = "id_notification")
    private Integer idNotification;

    @Column(name = "owner_id")
    private Integer ownerId;

    private String title;

    private String message;

    private String type;

    private Boolean read;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "temps_relatif")
    private String tempsRelatif;

    @Column(name = "id_reservation")
    private Integer idReservation;

    @Column(name = "id_reservation_request")
    private Long idReservationRequest;

    @Column(name = "montant_reservation")
    private BigDecimal montantReservation;

    @Column(name = "parking_label")
    private String parkingLabel;
}
