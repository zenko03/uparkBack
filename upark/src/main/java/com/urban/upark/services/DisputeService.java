package com.urban.upark.services;

import com.urban.upark.models.Dispute;
import com.urban.upark.repositories.DisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;

    @Autowired
    public DisputeService(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    public List<Dispute> getDisputesByUserId(Long userId) {
        return disputeRepository.findByUserId(userId);
    }

    public List<Dispute> getDisputesByReservationId(Long reservationId) {
        return disputeRepository.findByReservationId(reservationId);
    }

    public Optional<Dispute> getDisputeById(Long id) {
        return disputeRepository.findById(id);
    }

    public Dispute createDispute(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    public Dispute updateDispute(Long id, Dispute disputeDetails) {
        return disputeRepository.findById(id).map(dispute -> {
            dispute.setMotif(disputeDetails.getMotif());
            dispute.setDescription(disputeDetails.getDescription());
            if (disputeDetails.getReservation() != null) {
                dispute.setReservation(disputeDetails.getReservation());
            }
            return disputeRepository.save(dispute);
        }).orElse(null);
    }

    public void deleteDispute(Long id) {
        disputeRepository.deleteById(id);
    }
}
