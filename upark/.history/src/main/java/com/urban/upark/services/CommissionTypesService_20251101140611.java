package com.urban.upark.services;

import com.urban.upark.models.CommissionTypes;
import com.urban.upark.repositories.CommissionTypesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommissionTypesService {

    private final CommissionTypesRepository commissionTypesRepository;

    public List<CommissionTypes> findAll() {
        return commissionTypesRepository.findAll();
    }

    public Optional<CommissionTypes> findById(int id) {
        return commissionTypesRepository.findById(id);
    }

    public CommissionTypes save(CommissionTypes commissionTypes) {
        return commissionTypesRepository.save(commissionTypes);
    }

    public void deleteById(int id) {
        commissionTypesRepository.deleteById(id);
    }
}