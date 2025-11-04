package com.urban.upark.services;

import com.urban.upark.models.CommissionVehicles;
import com.urban.upark.repositories.CommissionVehiclesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommissionVehiclesService {

    private final CommissionVehiclesRepository commissionVehiclesRepository;

    public List<CommissionVehicles> findAll() {
        return commissionVehiclesRepository.findAll();
    }

    public Optional<CommissionVehicles> findById(int id) {
        return commissionVehiclesRepository.findById(id);
    }

    public CommissionVehicles save(CommissionVehicles commissionVehicles) {
        return commissionVehiclesRepository.save(commissionVehicles);
    }

    public void deleteById(int id) {
        commissionVehiclesRepository.deleteById(id);
    }
}