package com.urban.upark.controllers;

import com.urban.upark.models.PaymentStatus;
import com.urban.upark.services.PaymentStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment-status")
@RequiredArgsConstructor
public class PaymentStatusController {

    private final PaymentStatusService paymentStatusService;

    @GetMapping
    public List<PaymentStatus> getAllPaymentStatus() {
        return paymentStatusService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentStatus> getPaymentStatusById(@PathVariable int id) {
        Optional<PaymentStatus> paymentStatus = paymentStatusService.findById(id);
        if (paymentStatus.isPresent()) {
            return ResponseEntity.ok(paymentStatus.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PaymentStatus createPaymentStatus(@RequestBody PaymentStatus paymentStatus) {
        return paymentStatusService.save(paymentStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentStatus> updatePaymentStatus(@PathVariable int id, @RequestBody PaymentStatus paymentStatusDetails) {
        Optional<PaymentStatus> paymentStatus = paymentStatusService.findById(id);
        if (paymentStatus.isPresent()) {
            PaymentStatus updatedPaymentStatus = paymentStatus.get();
            updatedPaymentStatus.setLabel(paymentStatusDetails.getLabel());
            updatedPaymentStatus.setValue(paymentStatusDetails.getValue());
            return ResponseEntity.ok(paymentStatusService.save(updatedPaymentStatus));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentStatus(@PathVariable int id) {
        Optional<PaymentStatus> paymentStatus = paymentStatusService.findById(id);
        if (paymentStatus.isPresent()) {
            paymentStatusService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}