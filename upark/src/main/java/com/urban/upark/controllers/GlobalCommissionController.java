package com.urban.upark.controllers;

import com.urban.upark.models.GlobalCommission;
import com.urban.upark.services.GlobalCommissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/global-commission")
@RequiredArgsConstructor
public class GlobalCommissionController {

    private final GlobalCommissionService globalCommissionService;

    @GetMapping
    public List<GlobalCommission> getAllGlobalCommissions() {
        return globalCommissionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalCommission> getGlobalCommissionById(@PathVariable int id) {
        Optional<GlobalCommission> globalCommission = globalCommissionService.findById(id);
        if (globalCommission.isPresent()) {
            return ResponseEntity.ok(globalCommission.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public GlobalCommission createGlobalCommission(@RequestBody GlobalCommission globalCommission) {
        return globalCommissionService.save(globalCommission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalCommission> updateGlobalCommission(@PathVariable int id, @RequestBody GlobalCommission globalCommissionDetails) {
        Optional<GlobalCommission> globalCommission = globalCommissionService.findById(id);
        if (globalCommission.isPresent()) {
            GlobalCommission updatedGlobalCommission = globalCommission.get();
            updatedGlobalCommission.setRate(globalCommissionDetails.getRate());
            updatedGlobalCommission.setCreationDate(globalCommissionDetails.getCreationDate());
            return ResponseEntity.ok(globalCommissionService.save(updatedGlobalCommission));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGlobalCommission(@PathVariable int id) {
        Optional<GlobalCommission> globalCommission = globalCommissionService.findById(id);
        if (globalCommission.isPresent()) {
            globalCommissionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}