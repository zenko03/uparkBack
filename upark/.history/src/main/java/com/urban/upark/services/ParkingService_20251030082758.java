package com.urban.upark.services;

import com.urban.upark.models.Parking;
import com.urban.upark.repositories.ParkingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> findById(int id) {
        return parkingRepository.findById(id);
    }

    public Parking save(Parking parking) {
        return parkingRepository.save(parking);
    }

    public void deleteById(int id) {
        parkingRepository.deleteById(id);
    }
}