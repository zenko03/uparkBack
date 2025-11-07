package com.urban.upark.controllers;

import com.urban.upark.models.ReservationDelay;
import com.urban.upark.services.ReservationDelayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservation-delay")
@RequiredArgsConstructor
public class ReservationDelayController {

    private final ReservationDelayService reservationDelayService;

    /**
     * Récupérer tous les délais de réservation
     */
    @GetMapping
    public List<ReservationDelay> getAllReservationDelays() {
        return reservationDelayService.findAll();
    }

    /**
     * Récupérer un délai de réservation par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDelay> getReservationDelayById(@PathVariable int id) {
        Optional<ReservationDelay> reservationDelay = reservationDelayService.findById(id);
        if (reservationDelay.isPresent()) {
            return ResponseEntity.ok(reservationDelay.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer le délai de réservation actif (le plus récent)
     */
    @GetMapping("/active")
    public ResponseEntity<ReservationDelay> getActiveReservationDelay() {
        Optional<ReservationDelay> activeDelay = reservationDelayService.getActiveDelay();
        if (activeDelay.isPresent()) {
            return ResponseEntity.ok(activeDelay.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Créer un nouveau délai de réservation
     */
    @PostMapping
    public ResponseEntity<ReservationDelay> createReservationDelay(@RequestBody ReservationDelay reservationDelay) {
        try {
            ReservationDelay savedDelay = reservationDelayService.save(reservationDelay);
            return ResponseEntity.ok(savedDelay);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mettre à jour un délai de réservation
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDelay> updateReservationDelay(@PathVariable int id, 
                                                                  @RequestBody ReservationDelay reservationDelayDetails) {
        try {
            Optional<ReservationDelay> reservationDelayOpt = reservationDelayService.findById(id);
            if (reservationDelayOpt.isPresent()) {
                ReservationDelay existingDelay = reservationDelayOpt.get();
                
                // Mettre à jour les champs
                existingDelay.setDelayInHours(reservationDelayDetails.getDelayInHours());
                existingDelay.setDelayInMinutes(reservationDelayDetails.getDelayInMinutes());
                
                ReservationDelay updatedDelay = reservationDelayService.save(existingDelay);
                return ResponseEntity.ok(updatedDelay);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un délai de réservation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationDelay(@PathVariable int id) {
        try {
            Optional<ReservationDelay> reservationDelay = reservationDelayService.findById(id);
            if (reservationDelay.isPresent()) {
                reservationDelayService.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Vérifier si une réservation est possible pour une date donnée
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkReservationAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDateTime) {
        try {
            boolean isPossible = reservationDelayService.isReservationPossible(requestedDateTime);
            return ResponseEntity.ok(isPossible);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtenir le délai minimum en minutes pour une réservation
     */
    @GetMapping("/minimum-delay")
    public ResponseEntity<Integer> getMinimumDelayInMinutes() {
        try {
            int minimumDelay = reservationDelayService.getMinimumDelayInMinutes();
            return ResponseEntity.ok(minimumDelay);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtenir la date la plus proche pour laquelle une réservation est possible
     */
    @GetMapping("/earliest-possible-time")
    public ResponseEntity<LocalDateTime> getEarliestPossibleReservationTime() {
        try {
            LocalDateTime earliestTime = reservationDelayService.getEarliestPossibleReservationTime();
            return ResponseEntity.ok(earliestTime);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}