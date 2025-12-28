package com.urban.upark.services;

import com.urban.upark.models.ReservationRequest;
import com.urban.upark.repositories.ReservationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationRequestService {

    private final ReservationRequestRepository reservationRequestRepository;

    public List<ReservationRequest> findAll() {
        return reservationRequestRepository.findAll();
    }

    public Optional<ReservationRequest> findById(Long id) {
        return reservationRequestRepository.findById(id);
    }

    @Transactional
    public ReservationRequest save(ReservationRequest request) {
        return reservationRequestRepository.save(request);
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRequestRepository.deleteById(id);
    }
}
