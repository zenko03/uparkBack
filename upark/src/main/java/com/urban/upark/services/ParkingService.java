package com.urban.upark.services;

import com.urban.upark.models.AvailabilitiesDate;
import com.urban.upark.models.AvailabilitiesFrequence;
import com.urban.upark.models.Parking;
import com.urban.upark.models.Vehicles;
import com.urban.upark.models.AnnouncementsVehicles;
import com.urban.upark.repositories.ParkingRepository;
import com.urban.upark.repositories.ParkingVehiclesRepository;
import com.urban.upark.repositories.AnnouncementsVehiclesRepository;
import com.urban.upark.repositories.AvailabilitiesDateRepository;
import com.urban.upark.repositories.AvailabilitiesFrequenceRepository;
import com.urban.upark.repositories.VehiclesRepository;
import com.urban.upark.dto.parking.ParkingAvailabilityResponse;
import com.urban.upark.dto.parking.VehicleAvailability;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingVehiclesRepository parkingVehiclesRepository;
    private final AvailabilitiesDateRepository availabilitiesDateRepository;
    private final AvailabilitiesFrequenceRepository availabilitiesFrequenceRepository;
    private final AnnouncementsVehiclesRepository announcementsVehiclesRepository;
    private final VehiclesRepository vehiclesRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> findById(int id) {
        return parkingRepository.findById(id);
    }

    @Transactional
    public Parking save(Parking parking) {
        // If the parking has an ID, it's an update - use regular save
        if (parking.getId_Parking() > 0) {
            return parkingRepository.save(parking);
        }
        
        // For new parking, use native query to handle geography
        String sql = "INSERT INTO parking (label, hourly_rate, description, localisation, id_users) " +
                    "VALUES (:label, :hourlyRate, :description, ST_GeogFromText(:localisation), :userId)";
        
        entityManager.createNativeQuery(sql)
                .setParameter("label", parking.getLabel())
                .setParameter("hourlyRate", parking.getHourlyRate())
                .setParameter("description", parking.getDescription())
                .setParameter("localisation", parking.getLocalisation())
                .setParameter("userId", parking.getUser().getId_Users())
                .executeUpdate();
        
        // Retrieve the newly created parking
        return parkingRepository.findAll().stream()
                .filter(p -> p.getLabel().equals(parking.getLabel())
                        && p.getUser().getId_Users() == parking.getUser().getId_Users())
                .findFirst()
                .orElse(parking);
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
     * @param numberOfVehicles Nombre de véhicules souhaité
     * @param sortBy Critère de tri (price, distance)
     * @return Liste de parkings filtrés et triés
     */
    public List<Parking> searchParkings(
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer vehicleType,
            Integer numberOfVehicles,
            String sortBy) {

        // Récupérer tous les parkings
        List<Parking> parkings = parkingRepository.findAll();

        // Appliquer les filtres
        return parkings.stream()
                .filter(parking -> filterByPrice(parking, minPrice, maxPrice))
                .filter(parking -> filterByVehicleType(parking, vehicleType))
                .filter(parking -> filterByNumberOfVehicles(parking, vehicleType, numberOfVehicles))
                .filter(parking -> filterByAvailability(parking, startDate, endDate))
                .sorted(getSortComparator(sortBy, startDate))
                .collect(Collectors.toList());
    }

    /**
     * Recherche de parkings par adresse/localisation
     */
    public List<Parking> searchByAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return parkingRepository.findAll();
        }
        return parkingRepository.findByAddressContaining(address.trim());
    }

    /**
     * Recherche de parkings par coordonnées géographiques
     */
    public List<Parking> searchByLocation(String location, double radiusKm) {
        // Convertir le rayon en mètres (PostGIS utilise des mètres)
        double radiusMeters = radiusKm * 1000;
        return parkingRepository.findParkingsByLocation(location, radiusMeters);
    }

    /**
     * Recherche combinée : adresse + coordonnées
     */
    public List<Parking> searchByLocationAndAddress(String address, String location, double radiusKm) {
        List<Parking> parkings;
        
        if (location != null && !location.trim().isEmpty()) {
            // Priorité à la recherche géolocalisée
            parkings = searchByLocation(location, radiusKm);
        } else {
            // Recherche par adresse si pas de coordonnées
            parkings = searchByAddress(address);
        }
        
        return parkings;
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
     * Filtre les parkings par nombre de véhicules disponibles
     */
    private boolean filterByNumberOfVehicles(Parking parking, Integer vehicleType, Integer numberOfVehicles) {
        if (numberOfVehicles == null) {
            return true;
        }
        
        // Vérifier si le parking a assez de places pour le type de véhicule demandé
        return parkingVehiclesRepository.findByParkingId(parking.getId_Parking())
                .stream()
                .filter(pv -> vehicleType == null || pv.getVehicle().getId_Vehicles() == vehicleType)
                .mapToInt(pv -> pv.getNumbers())
                .sum() >= numberOfVehicles;
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

    /**
     * Obtenir la disponibilité d'un parking par type de véhicule
     */
    public ParkingAvailabilityResponse getParkingAvailability(int parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Récupérer le parking
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        // Récupérer tous les types de véhicules
        List<Vehicles> allVehicles = vehiclesRepository.findAll();
        
        List<VehicleAvailability> vehicleAvailabilities = new ArrayList<>();

        for (Vehicles vehicle : allVehicles) {
            // Utiliser Announcements_vehicles pour avoir la capacité PROPOSÉE à la réservation
            // (pas Parking_vehicles qui est la capacité physique totale)
            List<AnnouncementsVehicles> announcements = announcementsVehiclesRepository
                    .findByParkingAndVehicleType(parkingId, vehicle.getId_Vehicles());
            
            if (!announcements.isEmpty()) {
                // Calculer la capacité totale PROPOSÉE pour ce type de véhicule
                int totalCapacity = announcements.stream()
                        .mapToInt(AnnouncementsVehicles::getNumbers)
                        .sum();
                
                // Calculer la capacité disponible en tenant compte des réservations existantes
                int reservedCapacity = calculateReservedCapacity(announcements, startDateTime, endDateTime);
                int availableCapacity = Math.max(0, totalCapacity - reservedCapacity);
                
                System.out.println("🚗 " + vehicle.getTypes() + " - Capacité proposée: " + totalCapacity 
                    + ", Réservée: " + reservedCapacity + ", Disponible: " + availableCapacity);
                
                vehicleAvailabilities.add(VehicleAvailability.builder()
                        .vehicleTypeId(vehicle.getId_Vehicles())
                        .vehicleType(vehicle.getTypes())
                        .vehicleIcon(vehicle.getIcon())
                        .totalCapacity(totalCapacity)
                        .availableCapacity(availableCapacity)
                        .isAvailable(availableCapacity > 0)
                        .build());
            } else {
                // Ce parking n'accepte pas ce type de véhicule (aucune annonce)
                vehicleAvailabilities.add(VehicleAvailability.builder()
                        .vehicleTypeId(vehicle.getId_Vehicles())
                        .vehicleType(vehicle.getTypes())
                        .vehicleIcon(vehicle.getIcon())
                        .totalCapacity(0)
                        .availableCapacity(0)
                        .isAvailable(false)
                        .build());
            }
        }

        return ParkingAvailabilityResponse.builder()
                .parkingId(parking.getId_Parking())
                .parkingName(parking.getLabel())
                .description(parking.getDescription())
                .availabilitySchedule(generateAvailabilitySchedule(parkingId))
                .vehicleAvailabilities(vehicleAvailabilities)
                .build();
    }
    
    /**
     * Génère le texte de disponibilité horaire (ex: "Du lundi au vendredi à 10:30-18:30")
     */
    private String generateAvailabilitySchedule(int parkingId) {
        try {
            // Récupérer toutes les disponibilités de fréquence pour ce parking
            List<AvailabilitiesFrequence> frequences = availabilitiesFrequenceRepository.findAll().stream()
                    .filter(af -> af.getAnnouncementsVehicles() != null 
                            && af.getAnnouncementsVehicles().getParkingVehicles() != null
                            && af.getAnnouncementsVehicles().getParkingVehicles().getParking() != null
                            && af.getAnnouncementsVehicles().getParkingVehicles().getParking().getId_Parking() == parkingId)
                    .collect(Collectors.toList());
            
            if (frequences.isEmpty()) {
                return "24h/24 et 7j/7";
            }
            
            // Grouper par horaires
            Map<String, List<String>> scheduleByTime = new HashMap<>();
            for (AvailabilitiesFrequence af : frequences) {
                LocalTime startHour = af.getStartHour();
                LocalTime endHour = af.getEndHour();
                String dayName = af.getDayOfWeek() != null ? af.getDayOfWeek().getDayName() : "";
                
                if (!dayName.isEmpty()) {
                    String timeSlot = startHour + "-" + endHour;
                    scheduleByTime.computeIfAbsent(timeSlot, k -> new ArrayList<>()).add(dayName);
                }
            }
            
            if (scheduleByTime.isEmpty()) {
                return "24h/24 et 7j/7";
            }
            
            // Formater le texte
            StringBuilder schedule = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : scheduleByTime.entrySet()) {
                if (schedule.length() > 0) schedule.append(", ");
                
                List<String> days = entry.getValue();
                String timeSlot = entry.getKey();
                
                // Simplifier si c'est du lundi au vendredi
                if (days.size() >= 5 && days.contains("Lundi") && days.contains("Vendredi")) {
                    schedule.append("Du lundi au vendredi à ").append(timeSlot);
                } else if (days.size() == 7) {
                    schedule.append("7j/7 à ").append(timeSlot);
                } else {
                    schedule.append(String.join(", ", days)).append(" à ").append(timeSlot);
                }
            }
            
            return schedule.toString();
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du planning: " + e.getMessage());
            return "Disponibilités à vérifier";
        }
    }
    
    /**
     * Calcule le nombre de places réservées pour une liste d'annonces durant une période donnée
     * 
     * @param announcements Liste des annonces de véhicules pour un parking et un type de véhicule
     * @param startDateTime Début de la période recherchée
     * @param endDateTime Fin de la période recherchée
     * @return Nombre de places déjà réservées durant cette période
     */
    private int calculateReservedCapacity(List<AnnouncementsVehicles> announcements, 
                                         LocalDateTime startDateTime, 
                                         LocalDateTime endDateTime) {
        // Récupérer les IDs des annonces
        List<Integer> announcementIds = announcements.stream()
                .map(AnnouncementsVehicles::getId_Announcements_vehicles)
                .collect(Collectors.toList());
        
        if (announcementIds.isEmpty()) {
            return 0;
        }
        
        // Compter les réservations qui chevauchent la période demandée
        // Une réservation chevauche si:
        // - Elle commence avant la fin de notre période ET
        // - Elle se termine après le début de notre période
        String sql = "SELECT COUNT(rv.id_reservation_vehicles) " +
                    "FROM reservation_vehicles rv " +
                    "JOIN reservation r ON rv.id_reservation = r.id_reservation " +
                    "WHERE rv.id_announcements_vehicles IN :announcementIds " +
                    "AND r.start_datetime < :endDateTime " +
                    "AND r.end_datetime > :startDateTime " +
                    "AND r.id_reservation_status != 3"; // Exclure les réservations annulées (status 3)
        
        Long count = (Long) entityManager.createNativeQuery(sql)
                .setParameter("announcementIds", announcementIds)
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .getSingleResult();
        
        return count != null ? count.intValue() : 0;
    }
}