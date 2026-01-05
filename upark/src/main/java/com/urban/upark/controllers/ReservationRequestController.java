package com.urban.upark.controllers;

import com.urban.upark.models.Reservation;
import com.urban.upark.models.ReservationRequest;
import com.urban.upark.services.ReservationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation-requests")
@RequiredArgsConstructor
public class ReservationRequestController {

    private final ReservationRequestService reservationRequestService;

    @GetMapping
    public List<ReservationRequest> getAllReservationRequests() {
        return reservationRequestService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationRequest> getReservationRequestById(@PathVariable Long id) {
        return reservationRequestService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Demandes reçues par un propriétaire
    @GetMapping("/owner/{ownerId}")
    public List<ReservationRequest> getRequestsByOwner(@PathVariable Long ownerId) {
        return reservationRequestService.findByOwnerId(ownerId);
    }

    // Demandes envoyées par un client
    @GetMapping("/requester/{requesterId}")
    public List<ReservationRequest> getRequestsByRequester(@PathVariable Long requesterId) {
        return reservationRequestService.findByRequesterId(requesterId);
    }

    // Demandes par statut (10=En attente, 20=Acceptée, 25=Refusée)
    @GetMapping("/status/{state}")
    public List<ReservationRequest> getRequestsByState(@PathVariable Short state) {
        return reservationRequestService.findByState(state);
    }

    @PostMapping
    public ReservationRequest createReservationRequest(@RequestBody CreateReservationRequestDTO dto) {
        System.out.println("📥 Réception demande de réservation:");
        System.out.println("  - Requester ID: " + dto.getRequesterId());
        System.out.println("  - Announcement ID: " + dto.getAnnouncementId());
        System.out.println("  - Start: " + dto.getStartDateTime());
        System.out.println("  - End: " + dto.getEndDateTime());
        System.out.println("  - Total Gain: " + dto.getTotalGain());
        System.out.println("  - Selected Vehicles: " + (dto.getSelectedVehicles() != null ? dto.getSelectedVehicles().size() + " type(s)" : "NULL"));
        if (dto.getSelectedVehicles() != null) {
            dto.getSelectedVehicles().forEach(v -> 
                System.out.println("    → vehicleTypeId=" + v.getVehicleTypeId() + ", quantity=" + v.getQuantity())
            );
        }
        
        return reservationRequestService.createReservationRequest(
                dto.getRequesterId(),
                dto.getAnnouncementId(),
                dto.getStartDateTime(),
                dto.getEndDateTime(),
                dto.getTotalGain(),
                dto.getSelectedVehicles()
        );
    }

    // DTO pour recevoir les données du frontend
    @lombok.Data
    public static class CreateReservationRequestDTO {
        private Integer requesterId;
        private Integer announcementId;
        private java.time.LocalDateTime startDateTime;
        private java.time.LocalDateTime endDateTime;
        private Float totalGain;
        private java.util.List<com.urban.upark.dto.reservation.VehicleSelection> selectedVehicles;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationRequest> updateReservationRequest(@PathVariable Long id, @RequestBody ReservationRequest requestDetails) {
        return reservationRequestService.findById(id)
                .map(request -> {
                    request.setStartDateTime(requestDetails.getStartDateTime());
                    request.setEndDateTime(requestDetails.getEndDateTime());
                    request.setTotalGain(requestDetails.getTotalGain());
                    request.setState(requestDetails.getState());
                    request.setAnnouncement(requestDetails.getAnnouncement());
                    request.setRequester(requestDetails.getRequester());
                    return ResponseEntity.ok(reservationRequestService.save(request));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Accepter une demande (crée réservation)
    @PutMapping("/{id}/accept")
    public ResponseEntity<ReservationRequest> acceptRequest(@PathVariable Long id) {
        try {
            ReservationRequest request = reservationRequestService.acceptRequest(id);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Refuser une demande
    @PutMapping("/{id}/reject")
    public ResponseEntity<ReservationRequest> rejectRequest(@PathVariable Long id) {
        try {
            ReservationRequest request = reservationRequestService.rejectRequest(id);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Finaliser le paiement et créer la réservation (NOUVEAU)
    @PostMapping("/{id}/finalize")
    public ResponseEntity<Reservation> finalizeReservation(
        @PathVariable Long id,
        @RequestParam String paymentMethod
    ) {
        try {
            Reservation reservation = reservationRequestService.finalizeReservation(id, paymentMethod);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationRequest(@PathVariable Long id) {
        if (reservationRequestService.findById(id).isPresent()) {
            reservationRequestService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
