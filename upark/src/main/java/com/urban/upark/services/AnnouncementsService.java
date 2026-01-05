package com.urban.upark.services;

import com.urban.upark.dto.AnnouncementVehicleDTO;
import com.urban.upark.dto.AvailabilityDateDTO;
import com.urban.upark.dto.AvailabilityFrequenceDTO;
import com.urban.upark.dto.CreateAnnouncementDTO;
import com.urban.upark.models.*;
import com.urban.upark.repositories.AnnouncementsRepository;
import com.urban.upark.repositories.ParkingRepository;
import com.urban.upark.repositories.ParkingVehiclesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementsService {

    private final AnnouncementsRepository announcementsRepository;
    private final ParkingRepository parkingRepository;
    private final ParkingVehiclesRepository parkingVehiclesRepository;
    private final AnnouncementsVehiclesService announcementsVehiclesService;
    private final AvailabilitiesDateService availabilitiesDateService;
    private final AvailabilitiesFrequenceService availabilitiesFrequenceService;

    public List<Announcements> findAll() {
        return announcementsRepository.findAll();
    }

    public Optional<Announcements> findById(int id) {
        return announcementsRepository.findById(id);
    }

    public List<Announcements> findByParkingId(int parkingId) {
        return announcementsRepository.findByParkingId(parkingId);
    }

    public List<Announcements> findPublished() {
        return announcementsRepository.findByIsPublishedTrue();
    }

    public List<Announcements> findByUserId(int userId) {
        return announcementsRepository.findByUserId(userId);
    }

    @Transactional
    public Announcements save(Announcements announcement) {
        return announcementsRepository.save(announcement);
    }

    @Transactional
    public void deleteById(int id) {
        announcementsRepository.deleteById(id);
    }

    @Transactional
    public Announcements togglePublished(int id) {
        Announcements announcement = announcementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcement.setPublished(!announcement.isPublished());
        return announcementsRepository.save(announcement);
    }

    /**
     * Créer une annonce complète avec véhicules et disponibilités
     */
    @Transactional
    public Announcements createCompleteAnnouncement(CreateAnnouncementDTO dto) {
        // 1. Vérifier que le parking existe
        Parking parking = parkingRepository.findById(dto.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found with id: " + dto.getParkingId()));

        // 2. Créer l'annonce
        Announcements announcement = Announcements.builder()
                .description(dto.getDescription())
                .creationDate(LocalDateTime.now())
                .isPublished(dto.isPublished())
                .parking(parking)
                .build();

        Announcements savedAnnouncement = announcementsRepository.save(announcement);

        // 3. Créer les ANNOUNCEMENTS_VEHICLES
        if (dto.getVehicles() != null && !dto.getVehicles().isEmpty()) {
            for (AnnouncementVehicleDTO vehicleDTO : dto.getVehicles()) {
                // Récupérer le ParkingVehicle correspondant
                ParkingVehicles parkingVehicle = parkingVehiclesRepository.findById(vehicleDTO.getParkingVehicleId())
                        .orElseThrow(() -> new RuntimeException("ParkingVehicle not found with id: " + vehicleDTO.getParkingVehicleId()));

                // Vérifier que le nombre de places demandé ne dépasse pas la capacité
                if (vehicleDTO.getNumbers() > parkingVehicle.getNumbers()) {
                    throw new RuntimeException("Cannot offer more places (" + vehicleDTO.getNumbers() + 
                            ") than available in parking (" + parkingVehicle.getNumbers() + ")");
                }

                // Créer AnnouncementsVehicles
                AnnouncementsVehicles announcementVehicle = AnnouncementsVehicles.builder()
                        .numbers(vehicleDTO.getNumbers())
                        .announcements(savedAnnouncement)
                        .parkingVehicles(parkingVehicle)
                        .build();

                AnnouncementsVehicles savedAnnouncementVehicle = announcementsVehiclesService.save(announcementVehicle);

                // 4. Créer les AVAILABILITIES_DATE pour ce véhicule
                if (dto.getAvailabilitiesDates() != null && !dto.getAvailabilitiesDates().isEmpty()) {
                    for (AvailabilityDateDTO dateDTO : dto.getAvailabilitiesDates()) {
                        AvailabilitiesDate availabilityDate = AvailabilitiesDate.builder()
                                .startDate(dateDTO.getStartDate())
                                .endDate(dateDTO.getEndDate())
                                .startHour(dateDTO.getStartHour())
                                .endHour(dateDTO.getEndHour())
                                .announcementsVehicles(savedAnnouncementVehicle)
                                .build();

                        availabilitiesDateService.save(availabilityDate);
                    }
                }

                // 5. Créer les AVAILABILITIES_FREQUENCE pour ce véhicule
                if (dto.getAvailabilitiesFrequence() != null && !dto.getAvailabilitiesFrequence().isEmpty()) {
                    for (AvailabilityFrequenceDTO freqDTO : dto.getAvailabilitiesFrequence()) {
                        // Créer un objet DaysWeek avec juste l'ID
                        DaysWeek dayOfWeek = new DaysWeek();
                        dayOfWeek.setId_Days_week(freqDTO.getDayOfWeekId());

                        AvailabilitiesFrequence availabilityFreq = AvailabilitiesFrequence.builder()
                                .startHour(freqDTO.getStartHour())
                                .endHour(freqDTO.getEndHour())
                                .dayOfWeek(dayOfWeek)
                                .announcementsVehicles(savedAnnouncementVehicle)
                                .build();

                        availabilitiesFrequenceService.save(availabilityFreq);
                    }
                }
            }
        }

        return savedAnnouncement;
    }
}
