package com.urban.upark.controllers;

import com.urban.upark.models.Reservation;
import com.urban.upark.services.ReservationService;
import com.urban.upark.dto.reservation.ReservationRequest;
import com.urban.upark.dto.reservation.PriceCalculationRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(request);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<BigDecimal> calculatePrice(@RequestBody PriceCalculationRequest request) {
        try {
            BigDecimal totalPrice = reservationService.calculateTotalPrice(request);
            return ResponseEntity.ok(totalPrice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(@PathVariable int userId) {
        return reservationService.findByUserId(userId);
    }

    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(@RequestBody PriceCalculationRequest request) {
        boolean available = reservationService.checkAvailability(request);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Reservation>> filterReservations(
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer parkingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Reservation> reservations = reservationService.findReservationsWithFilters(
                statusId, userId, parkingId, startDate, endDate);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filter/by-date-range")
    public ResponseEntity<List<Reservation>> filterReservationsByDateRange(
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Reservation> reservations = reservationService.findReservationsByDateRange(
                statusId, startDate, endDate);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable int id, @RequestBody Reservation reservationDetails) {
        try {
            Optional<Reservation> reservationOpt = reservationService.findById(id);
            if (reservationOpt.isPresent()) {
                Reservation existingReservation = reservationOpt.get();
                
                if (reservationDetails.getTotalPrice() != null) {
                    existingReservation.setTotalPrice(reservationDetails.getTotalPrice());
                }
                if (reservationDetails.getPaymentDate() != null) {
                    existingReservation.setPaymentDate(reservationDetails.getPaymentDate());
                }
                if (reservationDetails.getStartDateTime() != null) {
                    existingReservation.setStartDateTime(reservationDetails.getStartDateTime());
                }
                if (reservationDetails.getEndDateTime() != null) {
                    existingReservation.setEndDateTime(reservationDetails.getEndDateTime());
                }
                if (reservationDetails.getPaymentMethod() != null) {
                    existingReservation.setPaymentMethod(reservationDetails.getPaymentMethod());
                }
                
                
                return ResponseEntity.ok(reservationService.save(existingReservation));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
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