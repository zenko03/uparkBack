package com.urban.upark.services;

import com.urban.upark.models.CommissionReceived;
import com.urban.upark.repositories.CommissionReceivedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommissionReceivedService {

    private final CommissionReceivedRepository commissionReceivedRepository;

    public List<CommissionReceived> findAll() {
        return commissionReceivedRepository.findAll();
    }

    public Optional<CommissionReceived> findById(int id) {
        return commissionReceivedRepository.findById(id);
    }

    public CommissionReceived save(CommissionReceived commissionReceived) {
        return commissionReceivedRepository.save(commissionReceived);
    }

    public void deleteById(int id) {
        commissionReceivedRepository.deleteById(id);
    }
}