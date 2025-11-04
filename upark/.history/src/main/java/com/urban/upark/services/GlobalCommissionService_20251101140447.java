package com.urban.upark.services;

import com.urban.upark.models.GlobalCommission;
import com.urban.upark.repositories.GlobalCommissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlobalCommissionService {

    private final GlobalCommissionRepository globalCommissionRepository;

    public List<GlobalCommission> findAll() {
        return globalCommissionRepository.findAll();
    }

    public Optional<GlobalCommission> findById(int id) {
        return globalCommissionRepository.findById(id);
    }

    public GlobalCommission save(GlobalCommission globalCommission) {
        return globalCommissionRepository.save(globalCommission);
    }

    public void deleteById(int id) {
        globalCommissionRepository.deleteById(id);
    }
}