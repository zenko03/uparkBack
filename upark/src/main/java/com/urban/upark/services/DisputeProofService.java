package com.urban.upark.services;

import com.urban.upark.models.DisputeProof;
import com.urban.upark.repositories.DisputeProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisputeProofService {

    private final DisputeProofRepository disputeProofRepository;

    @Autowired
    public DisputeProofService(DisputeProofRepository disputeProofRepository) {
        this.disputeProofRepository = disputeProofRepository;
    }

    public List<DisputeProof> getAllDisputeProofs() {
        return disputeProofRepository.findAll();
    }

    public List<DisputeProof> getProofsByDisputeId(Long disputeId) {
        return disputeProofRepository.findByDispute_Id(disputeId);
    }

    public Optional<DisputeProof> getDisputeProofById(Long id) {
        return disputeProofRepository.findById(id);
    }

    public DisputeProof createDisputeProof(DisputeProof disputeProof) {
        return disputeProofRepository.save(disputeProof);
    }

    public DisputeProof updateDisputeProof(Long id, DisputeProof disputeProofDetails) {
        return disputeProofRepository.findById(id).map(proof -> {
            proof.setProofUrl(disputeProofDetails.getProofUrl());
            proof.setDispute(disputeProofDetails.getDispute());
            return disputeProofRepository.save(proof);
        }).orElse(null);
    }

    public void deleteDisputeProof(Long id) {
        disputeProofRepository.deleteById(id);
    }
}
