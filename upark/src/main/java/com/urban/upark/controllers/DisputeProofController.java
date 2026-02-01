package com.urban.upark.controllers;

import com.urban.upark.dto.dispute.DisputeProofUploadRequest;
import com.urban.upark.models.DisputeProof;
import com.urban.upark.services.DisputeProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dispute-proofs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DisputeProofController {

    private final DisputeProofService disputeProofService;

    @GetMapping
    public List<DisputeProof> getAllDisputeProofs() {
        return disputeProofService.getAllDisputeProofs();
    }

    @GetMapping("/dispute/{disputeId}")
    public List<DisputeProof> getProofsByDisputeId(@PathVariable Long disputeId) {
        return disputeProofService.getProofsByDisputeId(disputeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisputeProof> getDisputeProofById(@PathVariable Long id) {
        return disputeProofService.getDisputeProofById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DisputeProof createDisputeProof(@RequestBody DisputeProof disputeProof) {
        return disputeProofService.createDisputeProof(disputeProof);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisputeProof> updateDisputeProof(@PathVariable Long id, @RequestBody DisputeProof disputeProofDetails) {
        DisputeProof updatedProof = disputeProofService.updateDisputeProof(id, disputeProofDetails);
        if (updatedProof != null) {
            return ResponseEntity.ok(updatedProof);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisputeProof(@PathVariable Long id) {
        disputeProofService.deleteDisputeProof(id);
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/{disputeId}/upload")
    public ResponseEntity<DisputeProof> uploadProof(
            @PathVariable Long disputeId,
            @RequestBody DisputeProofUploadRequest request) {
        try {
            DisputeProof savedProof = disputeProofService.uploadAndSaveProof(disputeId, request);
            return ResponseEntity.ok(savedProof);
        } catch (RuntimeException e) {
            System.err.println("Erreur: Erreur upload preuve: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
