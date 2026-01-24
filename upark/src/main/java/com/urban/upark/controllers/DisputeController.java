package com.urban.upark.controllers;

import com.urban.upark.models.Dispute;
import com.urban.upark.services.DisputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/disputes")
@CrossOrigin(origins = "*")
public class DisputeController {

    private final DisputeService disputeService;

    @Autowired
    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @GetMapping
    public List<Dispute> getAllDisputes() {
        return disputeService.getAllDisputes();
    }

    @GetMapping("/user/{userId}")
    public List<Dispute> getDisputesByUserId(@PathVariable Long userId) {
        return disputeService.getDisputesByUserId(userId);
    }

    @GetMapping("/reservation/{reservationId}")
    public List<Dispute> getDisputesByReservationId(@PathVariable Long reservationId) {
        return disputeService.getDisputesByReservationId(reservationId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispute> getDisputeById(@PathVariable Long id) {
        return disputeService.getDisputeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Dispute createDispute(@RequestBody Dispute dispute) {
        return disputeService.createDispute(dispute);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispute> updateDispute(@PathVariable Long id, @RequestBody Dispute disputeDetails) {
        Dispute updatedDispute = disputeService.updateDispute(id, disputeDetails);
        if (updatedDispute != null) {
            return ResponseEntity.ok(updatedDispute);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispute(@PathVariable Long id) {
        disputeService.deleteDispute(id);
        return ResponseEntity.ok().build();
    }
}
