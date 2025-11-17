package com.urban.upark.controllers;

import com.urban.upark.models.Parking;
import com.urban.upark.services.ParkingService;
import com.urban.upark.dto.parking.ParkingAvailabilityResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parkings")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public List<Parking> getAllParkings() {
        // Utiliser une requête native pour obtenir les coordonnées en texte
        List<Parking> parkings = parkingService.findAll();
        
        // Convertir les coordonnées pour chaque parking
        for (Parking parking : parkings) {
            String locationText = (String) entityManager.createNativeQuery(
                "SELECT ST_AsText(localisation) FROM parking WHERE id_parking = :id")
                .setParameter("id", parking.getId_Parking())
                .getSingleResult();
            parking.setLocalisation(locationText);
        }
        
        return parkings;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parking> getParkingById(@PathVariable int id) {
        Optional<Parking> parking = parkingService.findById(id);
        if (parking.isPresent()) {
            return ResponseEntity.ok(parking.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Parking createParking(@RequestBody Parking parking) {
        return parkingService.save(parking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parking> updateParking(@PathVariable int id, @RequestBody Parking parkingDetails) {
        Optional<Parking> parking = parkingService.findById(id);
        if (parking.isPresent()) {
            Parking updatedParking = parking.get();
            updatedParking.setLabel(parkingDetails.getLabel());
            updatedParking.setHourlyRate(parkingDetails.getHourlyRate());
            updatedParking.setDescription(parkingDetails.getDescription());
            updatedParking.setLocalisation(parkingDetails.getLocalisation());
            updatedParking.setUser(parkingDetails.getUser());
            return ResponseEntity.ok(parkingService.save(updatedParking));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable int id) {
        Optional<Parking> parking = parkingService.findById(id);
        if (parking.isPresent()) {
            parkingService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Parking> searchParkings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer vehicleType,
            @RequestParam(required = false) Integer numberOfVehicles,
            @RequestParam(required = false, defaultValue = "price") String sortBy) {
        return parkingService.searchParkings(startDate, endDate, minPrice, maxPrice, vehicleType, numberOfVehicles, sortBy);
    }

    /**
     * Ex: /api/parkings/search/address?address=AMPITATAFIKA
     */
    @GetMapping("/search/address")
    public List<Parking> searchByAddress(
            @RequestParam(required = false) String address) {
        return parkingService.searchByAddress(address);
    }

    /**
     * Recherche  parkings par coordonnées géographiques
     */
    @GetMapping("/search/location")
    public List<Parking> searchByLocation(
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "10.0") double radius) {
        return parkingService.searchByLocation(location, radius);
    }

    /**
     * 
     * Ex: /api/parkings/search/combined?address=Paris&location=SRID=4326;POINT(2.3522 48.8566)&radius=5
     */
    @GetMapping("/search/combined")
    public List<Parking> searchCombined(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "10.0") double radius) {
        return parkingService.searchByLocationAndAddress(address, location, radius);
    }

    /**
     * Obtenir la disponibilité d'un parking par type de véhicule
     * Ex: /api/parkings/3/availability?startDateTime=2025-11-11T14:00:00&endDateTime=2025-11-11T16:00:00
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ParkingAvailabilityResponse> getParkingAvailability(
            @PathVariable int id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        
        // Si pas de dates spécifiées, utiliser maintenant et +1 heure
        LocalDateTime start = startDateTime != null ? startDateTime : LocalDateTime.now();
        LocalDateTime end = endDateTime != null ? endDateTime : LocalDateTime.now().plusHours(1);
        
        ParkingAvailabilityResponse availability = parkingService.getParkingAvailability(id, start, end);
        return ResponseEntity.ok(availability);
    }
}