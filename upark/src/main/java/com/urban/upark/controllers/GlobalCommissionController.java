package com.urban.upark.controllers;

import com.urban.upark.models.GlobalCommission;
import com.urban.upark.services.GlobalCommissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/commissions/global")
@RequiredArgsConstructor
public class GlobalCommissionController {

    private final GlobalCommissionService globalCommissionService;

    @GetMapping
    public List<GlobalCommission> getAllGlobalCommissions() {
        return globalCommissionService.findAll();
    }
    
    @GetMapping("/current")
    public ResponseEntity<GlobalCommission> getCurrentCommission() {
        Optional<GlobalCommission> commission = globalCommissionService.getCurrentCommission();
        return commission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/history")
    public List<GlobalCommission> getCommissionHistory() {
        return globalCommissionService.getCommissionHistory();
    }
    
    @PostMapping
    public ResponseEntity<GlobalCommission> createNewCommission(@RequestBody Map<String, BigDecimal> body) {
        BigDecimal rate = body.get("rate");
        if (rate == null) {
            return ResponseEntity.badRequest().build();
        }
        GlobalCommission commission = globalCommissionService.createNewCommission(rate);
        return ResponseEntity.ok(commission);
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