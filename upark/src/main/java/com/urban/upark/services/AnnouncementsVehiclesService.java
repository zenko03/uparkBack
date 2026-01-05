package com.urban.upark.services;

import com.urban.upark.models.AnnouncementsVehicles;
import com.urban.upark.repositories.AnnouncementsVehiclesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementsVehiclesService {

    private final AnnouncementsVehiclesRepository repository;

    public List<AnnouncementsVehicles> findAll() {
        return repository.findAll();
    }

    public Optional<AnnouncementsVehicles> findById(int id) {
        return repository.findById(id);
    }

    public List<AnnouncementsVehicles> findByParkingAndVehicleType(int parkingId, int vehicleTypeId) {
        return repository.findByParkingAndVehicleType(parkingId, vehicleTypeId);
    }

    @Transactional
    public AnnouncementsVehicles save(AnnouncementsVehicles announcementVehicle) {
        return repository.save(announcementVehicle);
    }

    @Transactional
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByAnnouncementId(int announcementId) {
        repository.deleteByAnnouncementId(announcementId);
    }
}
