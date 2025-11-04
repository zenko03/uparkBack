package com.urban.upark.controllers;

import com.urban.upark.models.CommissionTypes;
import com.urban.upark.services.CommissionTypesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commission-types")
@RequiredArgsConstructor
public class CommissionTypesController {

    private final CommissionTypesService commissionTypesService;

    @GetMapping
    public List<CommissionTypes> getAllCommissionTypes() {
        return commissionTypesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommissionTypes> getCommissionTypesById(@PathVariable int id) {
        Optional<CommissionTypes> commissionTypes = commissionTypesService.findById(id);
        if (commissionTypes.isPresent()) {
            return ResponseEntity.ok(commissionTypes.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public CommissionTypes createCommissionTypes(@RequestBody CommissionTypes commissionTypes) {
        return commissionTypesService.save(commissionTypes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommissionTypes> updateCommissionTypes(@PathVariable int id, @RequestBody CommissionTypes commissionTypesDetails) {
        Optional<CommissionTypes> commissionTypes = commissionTypesService.findById(id);
        if (commissionTypes.isPresent()) {
            CommissionTypes updatedCommissionTypes = commissionTypes.get();
            updatedCommissionTypes.setLabel(commissionTypesDetails.getLabel());
            return ResponseEntity.ok(commissionTypesService.save(updatedCommissionTypes));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionTypes(@PathVariable int id) {
        Optional<CommissionTypes> commissionTypes = commissionTypesService.findById(id);
        if (commissionTypes.isPresent()) {
            commissionTypesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}