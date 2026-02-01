package com.urban.upark.controllers;

import com.urban.upark.models.Parking;
import com.urban.upark.services.ParkingService;
import com.urban.upark.dto.parking.ParkingAvailabilityResponse;
import com.urban.upark.dto.parking.ParkingCreateRequest;
import com.urban.upark.dto.parking.ParkingUpdateRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/parkings")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public List<Parking> getAllParkings() {
        List<Parking> parkings = parkingService.findAll();
        
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
    public ResponseEntity<Parking> createParking(@RequestBody ParkingCreateRequest request) {
        try {
            System.out.println(" Création parking - User ID: " + request.getUserId());
            Parking parking = parkingService.createParkingWithVehicles(request);
            System.out.println(" Parking créé avec ID: " + parking.getId_Parking());
            return ResponseEntity.ok(parking);
        } catch (RuntimeException e) {
            System.err.println("Erreur: Erreur création parking: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParking(@PathVariable int id, @RequestBody ParkingUpdateRequest request) {
        try {
            System.out.println(" Mise à jour parking ID: " + id);
            System.out.println(" Données reçues: " + request);
            Parking updatedParking = parkingService.updateParkingWithVehicles(id, request);
            System.out.println(" Parking mis à jour: " + updatedParking.getId_Parking());
            return ResponseEntity.ok(updatedParking);
        } catch (RuntimeException e) {
            System.err.println("Erreur:z Erreur mise à jour parking " + id + ": " + e.getMessage());
            System.err.println("Erreur: Type d'erreur: " + e.getClass().getName());
            e.printStackTrace();
            
            // Retourner le message d'erreur réel pour déboguer
            if (e.getMessage() != null && e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            }
            
            // Pour toutes les autres erreurs, retourner 500 avec le message
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Erreur inconnue"
            ));
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

   
    @GetMapping("/search/address")
    public List<Parking> searchByAddress(
            @RequestParam(required = false) String address) {
        return parkingService.searchByAddress(address);
    }

    
    @GetMapping("/search/location")
    public List<Parking> searchByLocation(
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "10.0") double radius) {
        return parkingService.searchByLocation(location, radius);
    }

    
    @GetMapping("/search/combined")
    public List<Parking> searchCombined(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "10.0") double radius) {
        return parkingService.searchByLocationAndAddress(address, location, radius);
    }

    
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
    
    @GetMapping("/my-parkings")
    public ResponseEntity<List<Parking>> getMyParkings() {
        try {
            // Récupérer l'utilisateur depuis le JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Récupérer les parkings de cet utilisateur
            List<Parking> parkings = parkingService.findByUsername(username);
            return ResponseEntity.ok(parkings);
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
    }

    
    @GetMapping("/user/{userId}")
    @Deprecated
    public ResponseEntity<List<Parking>> getParkingsByUserId(@PathVariable int userId) {
        try {
            // Vérifier que l'utilisateur demande bien ses propres parkings
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Récupérer l'utilisateur connecté pour comparer les IDs
            List<Parking> userParkings = parkingService.findByUsername(username);
            if (!userParkings.isEmpty() && userParkings.get(0).getUser().getId_Users() != userId) {
                return ResponseEntity.status(403).build(); // Forbidden - pas ses parkings
            }
            
            return ResponseEntity.ok(parkingService.findByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    
    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<com.urban.upark.models.ParkingVehicles>> getParkingVehicles(@PathVariable int id) {
        try {
            List<com.urban.upark.models.ParkingVehicles> vehicles = parkingService.getParkingVehicles(id);
            return ResponseEntity.ok(vehicles);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Parking> toggleParkingActive(@PathVariable int id) {
        return ResponseEntity.ok(parkingService.toggleActive(id));
    }
}