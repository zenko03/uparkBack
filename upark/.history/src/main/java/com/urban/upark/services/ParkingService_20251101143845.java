package com.urban.upark.services;

import com.urban.upark.models.AvailabilitiesDate;
import com.urban.upark.models.Parking;
import com.urban.upark.models.Vehicles;
import com.urban.upark.repositories.ParkingRepository;
import com.urban.upark.repositories.ParkingVehiclesRepository;
import com.urban.upark.repositories.AvailabilitiesDateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingVehiclesRepository parkingVehiclesRepository;
    private final AvailabilitiesDateRepository availabilitiesDateRepository;

    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> findById(int id) {
        return parkingRepository.findById(id);
    }

    public Parking save(Parking parking) {
        return parkingRepository.save(parking);
    }

    public void deleteById(int id) {
        parkingRepository.deleteById(id);
    }

    /**
     * Recherche avancée de parkings avec filtres
     *
     * @param startDate Date de début souhaitée
     * @param endDate Date de fin souhaitée
     * @param minPrice Prix minimum
     * @param maxPrice Prix maximum
     * @param vehicleType Type de véhicule
     * @param sortBy Critère de tri (price, distance)
     * @return Liste de parkings filtrés et triés
     */
    public List<Parking> searchParkings(
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer vehicleType,
            String sortBy) {

        // Récupérer tous les parkings
        List<Parking> parkings = parkingRepository.findAll();

        // Appliquer les filtres
        return parkings.stream()
                .filter(parking -> filterByPrice(parking, minPrice, maxPrice))
                .filter(parking -> filterByVehicleType(parking, vehicleType))
                .filter(parking -> filterByAvailability(parking, startDate, endDate))
                .sorted(getSortComparator(sortBy, startDate))
                .collect(Collectors.toList());
    }

    /**
     * Filtre les parkings par prix
     */
    private boolean filterByPrice(Parking parking, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && parking.getHourlyRate().compareTo(minPrice) < 0) {
            return false;
        }
        if (maxPrice != null && parking.getHourlyRate().compareTo(maxPrice) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Filtre les parkings par type de véhicule
     */
    private boolean filterByVehicleType(Parking parking, Integer vehicleType) {
        if (vehicleType == null) {
            return true;
        }
        
        // Vérifier si le parking accepte ce type de véhicule
        return parkingVehiclesRepository.findByParkingId(parking.getId_Parking())
                .stream()
                .anyMatch(pv -> pv.getVehicle().getId_Vehicles() == vehicleType);
    }

    /**
     * Filtre les parkings par disponibilité
     */
    private boolean filterByAvailability(Parking parking, LocalDateTime startDate, LocalDateTime endDate) {
        // Si aucune date n'est spécifiée, le parking est considéré comme disponible
        if (startDate == null || endDate == null) {
            return true;
        }
        
        // Vérifier s'il y a des disponibilités qui chevauchent la période demandée
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        
        List<AvailabilitiesDate> availabilities =
            availabilitiesDateRepository.findOverlappingAvailabilities(
                parking.getId_Parking(), startLocalDate, endLocalDate);
        
        // Vérifier si au moins une disponibilité couvre la période demandée
        return availabilities.stream().anyMatch(availability ->
            isAvailabilityCoveringPeriod(availability, startDate, endDate));
    }
    
    /**
     * Vérifie si une disponibilité couvre une période donnée
     */
    private boolean isAvailabilityCoveringPeriod(
            com.urban.upark.models.AvailabilitiesDate availability,
            LocalDateTime requestedStart,
            LocalDateTime requestedEnd) {
        LocalDateTime availabilityStart = availability.getStartDate().atTime(availability.getStartHour());
        LocalDateTime availabilityEnd = availability.getEndDate().atTime(availability.getEndHour());
        
        return !requestedStart.isAfter(availabilityEnd) && !requestedEnd.isBefore(availabilityStart);
    }

    /**
     * Retourne le comparateur pour le tri
     */
    private java.util.Comparator<Parking> getSortComparator(String sortBy, LocalDateTime referenceDateTime) {
        switch (sortBy) {
            case "price":
                return java.util.Comparator.comparing(Parking::getHourlyRate);
            case "distance":
                // Pour l'instant, tri par ID comme placeholder
                // À implémenter avec la géolocalisation réelle
                return java.util.Comparator.comparing(Parking::getId_Parking);
            default:
                return java.util.Comparator.comparing(Parking::getHourlyRate);
        }
    }
}