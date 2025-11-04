package com.urban.upark.controllers;

import com.urban.upark.models.CommissionReceived;
import com.urban.upark.services.CommissionReceivedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commission-received")
@RequiredArgsConstructor
public class CommissionReceivedController {

    private final CommissionReceivedService commissionReceivedService;

    @GetMapping
    public List<CommissionReceived> getAllCommissionReceived() {
        return commissionReceivedService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommissionReceived> getCommissionReceivedById(@PathVariable int id) {
        Optional<CommissionReceived> commissionReceived = commissionReceivedService.findById(id);
        if (commissionReceived.isPresent()) {
            return ResponseEntity.ok(commissionReceived.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CommissionReceived createCommissionReceived(@RequestBody CommissionReceived commissionReceived) {
        return commissionReceivedService.save(commissionReceived);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommissionReceived> updateCommissionReceived(@PathVariable int id, @RequestBody CommissionReceived commissionReceivedDetails) {
        Optional<CommissionReceived> commissionReceived = commissionReceivedService.findById(id);
        if (commissionReceived.isPresent()) {
            CommissionReceived updatedCommissionReceived = commissionReceived.get();
            updatedCommissionReceived.setPrice(commissionReceivedDetails.getPrice());
            updatedCommissionReceived.setPaymentDate(commissionReceivedDetails.getPaymentDate());
            updatedCommissionReceived.setCommissionType(commissionReceivedDetails.getCommissionType());
            updatedCommissionReceived.setReservation(commissionReceivedDetails.getReservation());
            updatedCommissionReceived.setPaymentStatus(commissionReceivedDetails.getPaymentStatus());
            return ResponseEntity.ok(commissionReceivedService.save(updatedCommissionReceived));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionReceived(@PathVariable int id) {
        Optional<CommissionReceived> commissionReceived = commissionReceivedService.findById(id);
        if (commissionReceived.isPresent()) {
            commissionReceivedService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}