package com.urban.upark.services;

import com.urban.upark.models.AvailabilitiesDate;
import com.urban.upark.repositories.AvailabilitiesDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvailabilitiesDateService {

    private final AvailabilitiesDateRepository repository;

    public List<AvailabilitiesDate> findAll() {
        return repository.findAll();
    }

    public Optional<AvailabilitiesDate> findById(int id) {
        return repository.findById(id);
    }

    public List<AvailabilitiesDate> findOverlappingAvailabilities(int parkingId, LocalDate startDate, LocalDate endDate) {
        return repository.findOverlappingAvailabilities(parkingId, startDate, endDate);
    }

    @Transactional
    public AvailabilitiesDate save(AvailabilitiesDate availability) {
        return repository.save(availability);
    }

    @Transactional
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByAnnouncementVehicleId(int announcementVehicleId) {
        repository.deleteByAnnouncementVehicleId(announcementVehicleId);
    }
}
