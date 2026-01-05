package com.urban.upark.services;

import com.urban.upark.models.AvailabilitiesFrequence;
import com.urban.upark.repositories.AvailabilitiesFrequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvailabilitiesFrequenceService {

    private final AvailabilitiesFrequenceRepository repository;

    public List<AvailabilitiesFrequence> findAll() {
        return repository.findAll();
    }

    public Optional<AvailabilitiesFrequence> findById(int id) {
        return repository.findById(id);
    }

    public List<AvailabilitiesFrequence> findByParkingAndDayOfWeek(int parkingId, int dayOfWeekId) {
        return repository.findByParkingAndDayOfWeek(parkingId, dayOfWeekId);
    }

    @Transactional
    public AvailabilitiesFrequence save(AvailabilitiesFrequence availability) {
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
