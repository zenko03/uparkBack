package com.urban.upark.controllers;

import com.urban.upark.models.CommissionPartners;
import com.urban.upark.services.CommissionPartnersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commission-partners")
@RequiredArgsConstructor
public class CommissionPartnersController {

    private final CommissionPartnersService commissionPartnersService;

    @GetMapping
    public List<CommissionPartners> getAllCommissionPartners() {
        return commissionPartnersService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommissionPartners> getCommissionPartnersById(@PathVariable int id) {
        Optional<CommissionPartners> commissionPartners = commissionPartnersService.findById(id);
        if (commissionPartners.isPresent()) {
            return ResponseEntity.ok(commissionPartners.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CommissionPartners createCommissionPartners(@RequestBody CommissionPartners commissionPartners) {
        return commissionPartnersService.save(commissionPartners);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommissionPartners> updateCommissionPartners(@PathVariable int id, @RequestBody CommissionPartners commissionPartnersDetails) {
        Optional<CommissionPartners> commissionPartners = commissionPartnersService.findById(id);
        if (commissionPartners.isPresent()) {
            CommissionPartners updatedCommissionPartners = commissionPartners.get();
            updatedCommissionPartners.setRate(commissionPartnersDetails.getRate());
            updatedCommissionPartners.setCreationDate(commissionPartnersDetails.getCreationDate());
            updatedCommissionPartners.setUser(commissionPartnersDetails.getUser());
            return ResponseEntity.ok(commissionPartnersService.save(updatedCommissionPartners));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionPartners(@PathVariable int id) {
        Optional<CommissionPartners> commissionPartners = commissionPartnersService.findById(id);
        if (commissionPartners.isPresent()) {
            commissionPartnersService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}