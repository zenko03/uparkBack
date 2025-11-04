package com.urban.upark.services;

import com.urban.upark.models.PaymentStatus;
import com.urban.upark.repositories.PaymentStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;

    public List<PaymentStatus> findAll() {
        return paymentStatusRepository.findAll();
    }

    public Optional<PaymentStatus> findById(int id) {
        return paymentStatusRepository.findById(id);
    }

    public PaymentStatus save(PaymentStatus paymentStatus) {
        return paymentStatusRepository.save(paymentStatus);
    }

    public void deleteById(int id) {
        paymentStatusRepository.deleteById(id);
    }
}