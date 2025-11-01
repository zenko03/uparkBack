package com.urban.upark.controllers;

import com.urban.upark.models.Vehicles;
import com.urban.upark.services.VehiclesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehiclesController {

    @Autowired
    private VehiclesService vehiclesService;

    @GetMapping
    public List<Vehicles> getAllVehicles() {
        return vehiclesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicles> getVehicleById(@PathVariable int id) {
        Optional<Vehicles> vehicle = vehiclesService.findById(id);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Vehicles createVehicle(@RequestBody Vehicles vehicle) {
        return vehiclesService.save(vehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicles> updateVehicle(@PathVariable int id, @RequestBody Vehicles vehicleDetails) {
        Optional<Vehicles> vehicle = vehiclesService.findById(id);
        if (vehicle.isPresent()) {
            Vehicles updatedVehicle = vehicle.get();
            updatedVehicle.setTypes(vehicleDetails.getTypes());
            updatedVehicle.setIcon(vehicleDetails.getIcon());
            return ResponseEntity.ok(vehiclesService.save(updatedVehicle));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable int id) {
        Optional<Vehicles> vehicle = vehiclesService.findById(id);
        if (vehicle.isPresent()) {
            vehiclesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}