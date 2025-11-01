package com.urban.upark.services;

import com.urban.upark.models.Parking;
import com.urban.upark.models.Vehicles;
import com.urban.upark.repositories.ParkingRepository;
import com.urban.upark.repositories.ParkingVehiclesRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingVehiclesRepository parkingVehiclesRepository;

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
                .sorted(getSortComparator(sortBy))
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
     * Retourne le comparateur pour le tri
     */
    private java.util.Comparator<Parking> getSortComparator(String sortBy) {
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