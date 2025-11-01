package com.urban.upark.controllers;

import com.urban.upark.models.Reservation;
import com.urban.upark.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.findById(id);
        if (reservation.isPresent()) {
            return ResponseEntity.ok(reservation.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.save(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable int id, @RequestBody Reservation reservationDetails) {
        Optional<Reservation> reservation = reservationService.findById(id);
        if (reservation.isPresent()) {
            Reservation updatedReservation = reservation.get();
            updatedReservation.setTotalPrice(reservationDetails.getTotalPrice());
            updatedReservation.setCreationDate(reservationDetails.getCreationDate());
            updatedReservation.setPaymentDate(reservationDetails.getPaymentDate());
            updatedReservation.setUser(reservationDetails.getUser());
            updatedReservation.setReservationStatus(reservationDetails.getReservationStatus());
            return ResponseEntity.ok(reservationService.save(updatedReservation));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.findById(id);
        if (reservation.isPresent()) {
            reservationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}