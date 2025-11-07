package com.urban.upark.services;

import com.urban.upark.models.ParkingVehicles;
import com.urban.upark.repositories.ParkingVehiclesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingVehiclesService {

    private final ParkingVehiclesRepository parkingVehiclesRepository;

    public List<ParkingVehicles> findAll() {
        return parkingVehiclesRepository.findAll();
    }

    public Optional<ParkingVehicles> findById(int id) {
        return parkingVehiclesRepository.findById(id);
    }

    public ParkingVehicles save(ParkingVehicles parkingVehicles) {
        return parkingVehiclesRepository.save(parkingVehicles);
    }

    public void deleteById(int id) {
        parkingVehiclesRepository.deleteById(id);
    }

    public List<ParkingVehicles> findByParkingId(int parkingId) {
        return parkingVehiclesRepository.findByParkingId(parkingId);
    }
}