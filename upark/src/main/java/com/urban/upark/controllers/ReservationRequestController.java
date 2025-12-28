package com.urban.upark.controllers;

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

    @PostMapping
    public ReservationRequest createReservationRequest(@RequestBody ReservationRequest request) {
        return reservationRequestService.save(request);
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
                    return ResponseEntity.ok(reservationRequestService.save(request));
                })
                .orElse(ResponseEntity.notFound().build());
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
