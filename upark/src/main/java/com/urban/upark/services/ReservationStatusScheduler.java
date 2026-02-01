package com.urban.upark.services;

import com.urban.upark.models.Reservation;
import com.urban.upark.models.ReservationStatus;
import com.urban.upark.repositories.ReservationRepository;
import com.urban.upark.repositories.ReservationStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de planification pour la mise à jour automatique des statuts de réservations.
 * Vérifie toutes les 5 minutes les réservations dont le statut doit être mis à jour
 * en fonction des dates de début et de fin.
 * 
 * Statuts gérés :
 * - "à venir" (10) : Réservation confirmée, date de début dans le futur
 * - "En cours" (15) : Réservation active, entre date de début et date de fin
 * - "Terminée" (20) : Réservation passée, date de fin dépassée
 * - "Annulée" (25) : Réservation annulée manuellement
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationStatusScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationStatusRepository reservationStatusRepository;

    /**
     * Tâche planifiée exécutée toutes les 5 minutes (300 000 ms)
     * Met à jour automatiquement les statuts des réservations en fonction de la date/heure actuelle
     */
    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    @Transactional
    public void updateReservationStatuses() {
        log.info(" Début de la mise à jour automatique des statuts de réservations");
        
        // Utiliser UTC pour cohérence avec la base TIMESTAMPTZ
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        int updatedCount = 0;
        
        try {
            // Récupérer les statuts depuis la base de données
            ReservationStatus statusAVenir = getOrCreateStatus("à venir", 10);
            ReservationStatus statusEnCours = getOrCreateStatus("En cours", 15);
            ReservationStatus statusTerminee = getOrCreateStatus("Terminée", 20);

            // 1. Mettre à jour les réservations "à venir" → "En cours"
            // Condition : statut = "à venir" ET date de début passée ET date de fin future
            List<Reservation> reservationsAVenir = reservationRepository
                .findByReservationStatusIdAndStartDateTimeBefore(statusAVenir.getId_Reservation_status(), now);
            
            for (Reservation reservation : reservationsAVenir) {
                // Vérifier que la date de fin n'est pas encore passée
                if (reservation.getEndDateTime().isAfter(now)) {
                    reservation.setReservationStatus(statusEnCours);
                    reservationRepository.save(reservation);
                    updatedCount++;
                    log.info(" Réservation ID {} : 'à venir' → 'En cours' (Début: {}, Fin: {})",
                            reservation.getId_Reservation(), reservation.getStartDateTime(), reservation.getEndDateTime());
                } else {
                    // Si la date de fin est aussi passée, passer directement à "Terminée"
                    reservation.setReservationStatus(statusTerminee);
                    reservationRepository.save(reservation);
                    updatedCount++;
                    log.info(" Réservation ID {} : 'à venir' → 'Terminée' (période entièrement passée)",
                            reservation.getId_Reservation());
                }
            }

            // 2. Mettre à jour les réservations "En cours" → "Terminée"
            // Condition : statut = "En cours" ET date de fin passée
            List<Reservation> reservationsEnCours = reservationRepository
                .findByReservationStatusIdAndEndDateTimeBefore(statusEnCours.getId_Reservation_status(), now);
            
            for (Reservation reservation : reservationsEnCours) {
                reservation.setReservationStatus(statusTerminee);
                reservationRepository.save(reservation);
                updatedCount++;
                log.info(" Réservation ID {} : 'En cours' → 'Terminée'", reservation.getId_Reservation());
            }

            log.info("✨ Mise à jour automatique terminée : {} réservation(s) mise(s) à jour", updatedCount);
            
        } catch (Exception e) {
            log.error("Erreur: Erreur lors de la mise à jour automatique des statuts : {}", e.getMessage(), e);
        }
    }

    /**
     * Récupère un statut de réservation depuis la base ou le crée s'il n'existe pas
     * 
     * @param label Le label du statut (ex: "à venir", "En cours", etc.)
     * @param value La valeur numérique du statut
     * @return Le statut de réservation
     */
    private ReservationStatus getOrCreateStatus(String label, Integer value) {
        return reservationStatusRepository.findByLabel(label)
            .orElseGet(() -> {
                log.warn(" Statut '{}' non trouvé, création automatique", label);
                ReservationStatus status = ReservationStatus.builder()
                    .label(label)
                    .value(value)
                    .build();
                return reservationStatusRepository.save(status);
            });
    }
}
