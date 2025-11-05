package com.urban.upark.controllers;

import com.urban.upark.models.Parking;
import com.urban.upark.services.ParkingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public List<Parking> getAllParkings() {
        return parkingService.findAll();
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
}