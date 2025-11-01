package com.urban.upark.controllers;

import com.urban.upark.models.ReservationStatus;
import com.urban.upark.services.ReservationStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservation-status")
@RequiredArgsConstructor
public class ReservationStatusController {

    private final ReservationStatusService reservationStatusService;

    @GetMapping
    public List<ReservationStatus> getAllReservationStatus() {
        return reservationStatusService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationStatus> getReservationStatusById(@PathVariable int id) {
        Optional<ReservationStatus> reservationStatus = reservationStatusService.findById(id);
        if (reservationStatus.isPresent()) {
            return ResponseEntity.ok(reservationStatus.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ReservationStatus createReservationStatus(@RequestBody ReservationStatus reservationStatus) {
        return reservationStatusService.save(reservationStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationStatus> updateReservationStatus(@PathVariable int id, @RequestBody ReservationStatus reservationStatusDetails) {
        Optional<ReservationStatus> reservationStatus = reservationStatusService.findById(id);
        if (reservationStatus.isPresent()) {
            ReservationStatus updatedReservationStatus = reservationStatus.get();
            updatedReservationStatus.setLabel(reservationStatusDetails.getLabel());
            updatedReservationStatus.setValue(reservationStatusDetails.getValue());
            return ResponseEntity.ok(reservationStatusService.save(updatedReservationStatus));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationStatus(@PathVariable int id) {
        Optional<ReservationStatus> reservationStatus = reservationStatusService.findById(id);
        if (reservationStatus.isPresent()) {
            reservationStatusService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}