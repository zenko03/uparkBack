package com.urban.upark.services;

import com.urban.upark.models.ReservationStatus;
import com.urban.upark.repositories.ReservationStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationStatusService {

    private final ReservationStatusRepository reservationStatusRepository;

    public List<ReservationStatus> findAll() {
        return reservationStatusRepository.findAll();
    }

    public Optional<ReservationStatus> findById(int id) {
        return reservationStatusRepository.findById(id);
    }

    public ReservationStatus save(ReservationStatus reservationStatus) {
        return reservationStatusRepository.save(reservationStatus);
    }

    public void deleteById(int id) {
        reservationStatusRepository.deleteById(id);
    }
}