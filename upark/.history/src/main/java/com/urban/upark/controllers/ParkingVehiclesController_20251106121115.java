package com.urban.upark.controllers;

import com.urban.upark.models.ParkingVehicles;
import com.urban.upark.services.ParkingVehiclesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parking-vehicles")
@RequiredArgsConstructor
public class ParkingVehiclesController {

    private final ParkingVehiclesService parkingVehiclesService;

    @GetMapping
    public List<ParkingVehicles> getAllParkingVehicles() {
        return parkingVehiclesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingVehicles> getParkingVehicleById(@PathVariable int id) {
        Optional<ParkingVehicles> parkingVehicle = parkingVehiclesService.findById(id);
        if (parkingVehicle.isPresent()) {
            return ResponseEntity.ok(parkingVehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-parking/{parkingId}")
    public List<ParkingVehicles> getParkingVehiclesByParkingId(@PathVariable int parkingId) {
        return parkingVehiclesService.findByParkingId(parkingId);
    }

    @PostMapping
    public ParkingVehicles createParkingVehicle(@RequestBody ParkingVehicles parkingVehicle) {
        return parkingVehiclesService.save(parkingVehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingVehicles> updateParkingVehicle(@PathVariable int id, @RequestBody ParkingVehicles parkingVehicleDetails) {
        Optional<ParkingVehicles> parkingVehicle = parkingVehiclesService.findById(id);
        if (parkingVehicle.isPresent()) {
            ParkingVehicles updatedParkingVehicle = parkingVehicle.get();
            updatedParkingVehicle.setNumbers(parkingVehicleDetails.getNumbers());
            updatedParkingVehicle.setVehicle(parkingVehicleDetails.getVehicle());
            updatedParkingVehicle.setParking(parkingVehicleDetails.getParking());
            return ResponseEntity.ok(parkingVehiclesService.save(updatedParkingVehicle));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingVehicle(@PathVariable int id) {
        Optional<ParkingVehicles> parkingVehicle = parkingVehiclesService.findById(id);
        if (parkingVehicle.isPresent()) {
            parkingVehiclesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}