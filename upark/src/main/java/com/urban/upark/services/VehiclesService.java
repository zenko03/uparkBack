package com.urban.upark.services;

import com.urban.upark.models.Vehicles;
import com.urban.upark.repositories.VehiclesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;

    public List<Vehicles> findAll() {
        return vehiclesRepository.findAll();
    }

    public Optional<Vehicles> findById(int id) {
        return vehiclesRepository.findById(id);
    }

    public Vehicles save(Vehicles vehicle) {
        return vehiclesRepository.save(vehicle);
    }

    public void deleteById(int id) {
        vehiclesRepository.deleteById(id);
    }
}