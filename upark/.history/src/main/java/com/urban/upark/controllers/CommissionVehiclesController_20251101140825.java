package com.urban.upark.controllers;

import com.urban.upark.models.CommissionVehicles;
import com.urban.upark.services.CommissionVehiclesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commission-vehicles")
@RequiredArgsConstructor
public class CommissionVehiclesController {

    private final CommissionVehiclesService commissionVehiclesService;

    @GetMapping
    public List<CommissionVehicles> getAllCommissionVehicles() {
        return commissionVehiclesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommissionVehicles> getCommissionVehiclesById(@PathVariable int id) {
        Optional<CommissionVehicles> commissionVehicles = commissionVehiclesService.findById(id);
        if (commissionVehicles.isPresent()) {
            return ResponseEntity.ok(commissionVehicles.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CommissionVehicles createCommissionVehicles(@RequestBody CommissionVehicles commissionVehicles) {
        return commissionVehiclesService.save(commissionVehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommissionVehicles> updateCommissionVehicles(@PathVariable int id, @RequestBody CommissionVehicles commissionVehiclesDetails) {
        Optional<CommissionVehicles> commissionVehicles = commissionVehiclesService.findById(id);
        if (commissionVehicles.isPresent()) {
            CommissionVehicles updatedCommissionVehicles = commissionVehicles.get();
            updatedCommissionVehicles.setRate(commissionVehiclesDetails.getRate());
            updatedCommissionVehicles.setCreationDate(commissionVehiclesDetails.getCreationDate());
            updatedCommissionVehicles.setVehicle(commissionVehiclesDetails.getVehicle());
            return ResponseEntity.ok(commissionVehiclesService.save(updatedCommissionVehicles));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionVehicles(@PathVariable int id) {
        Optional<CommissionVehicles> commissionVehicles = commissionVehiclesService.findById(id);
        if (commissionVehicles.isPresent()) {
            commissionVehiclesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}