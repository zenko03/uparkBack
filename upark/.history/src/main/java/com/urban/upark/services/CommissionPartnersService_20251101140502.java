package com.urban.upark.services;

import com.urban.upark.models.CommissionPartners;
import com.urban.upark.repositories.CommissionPartnersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommissionPartnersService {

    private final CommissionPartnersRepository commissionPartnersRepository;

    public List<CommissionPartners> findAll() {
        return commissionPartnersRepository.findAll();
    }

    public Optional<CommissionPartners> findById(int id) {
        return commissionPartnersRepository.findById(id);
    }

    public CommissionPartners save(CommissionPartners commissionPartners) {
        return commissionPartnersRepository.save(commissionPartners);
    }

    public void deleteById(int id) {
        commissionPartnersRepository.deleteById(id);
    }
}