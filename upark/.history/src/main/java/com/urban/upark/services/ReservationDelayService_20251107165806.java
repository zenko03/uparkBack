package com.urban.upark.services;

import com.urban.upark.models.ReservationDelay;
import com.urban.upark.repositories.ReservationDelayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationDelayService {

    private final ReservationDelayRepository reservationDelayRepository;

    /**
     * Récupérer tous les délais de réservation
     */
    public List<ReservationDelay> findAll() {
        return reservationDelayRepository.findAll();
    }

    /**
     * Récupérer un délai par son ID
     */
    public Optional<ReservationDelay> findById(int id) {
        return reservationDelayRepository.findById(id);
    }

    /**
     * Créer ou mettre à jour un délai de réservation
     */
    public ReservationDelay save(ReservationDelay reservationDelay) {
        // Si c'est une nouvelle configuration, définir la date de création
        if (reservationDelay.getCreationDate() == null) {
            reservationDelay.setCreationDate(LocalDateTime.now());
        }
        return reservationDelayRepository.save(reservationDelay);
    }

    /**
     * Supprimer un délai de réservation
     */
    public void deleteById(int id) {
        reservationDelayRepository.deleteById(id);
    }

    /**
     * Récupérer le délai de réservation actif (le plus récent)
     */
    public Optional<ReservationDelay> getActiveDelay() {
        return reservationDelayRepository.findTopByOrderByCreationDateDesc();
    }

    /**
     * Vérifier si une réservation est possible selon le délai configuré
     */
    public boolean isReservationPossible(LocalDateTime requestedDateTime) {
        Optional<ReservationDelay> activeDelayOpt = getActiveDelay();
        
        if (activeDelayOpt.isEmpty()) {
            // Aucun délai configuré, la réservation est toujours possible
            return true;
        }

        ReservationDelay activeDelay = activeDelayOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Calculer la date limite de réservation
        LocalDateTime limitDateTime = now.plusHours(activeDelay.getDelayInHours())
                                              .plusMinutes(activeDelay.getDelayInMinutes());
        
        // La réservation est possible si la date demandée est après la date limite
        return requestedDateTime.isAfter(limitDateTime);
    }

    /**
     * Obtenir le délai minimum en minutes pour une réservation
     */
    public int getMinimumDelayInMinutes() {
        Optional<ReservationDelay> activeDelayOpt = getActiveDelay();
        
        if (activeDelayOpt.isEmpty()) {
            return 0; // Aucun délai minimum
        }

        ReservationDelay activeDelay = activeDelayOpt.get();
        return activeDelay.getDelayInHours() * 60 + activeDelay.getDelayInMinutes();
    }

    /**
     * Calculer la date la plus proche pour laquelle une réservation est possible
     */
    public LocalDateTime getEarliestPossibleReservationTime() {
        Optional<ReservationDelay> activeDelayOpt = getActiveDelay();
        
        if (activeDelayOpt.isEmpty()) {
            return LocalDateTime.now(); // Immédiatement possible
        }

        ReservationDelay activeDelay = activeDelayOpt.get();
        return LocalDateTime.now()
                           .plusHours(activeDelay.getDelayInHours())
                           .plusMinutes(activeDelay.getDelayInMinutes());
    }
}