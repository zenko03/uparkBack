package com.urban.upark.controllers;

import com.urban.upark.dto.parking.ImageUploadRequest;
import com.urban.upark.dto.parking.ParkingImageDTO;
import com.urban.upark.models.ParkingImage;
import com.urban.upark.services.ParkingImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-images")
@RequiredArgsConstructor
public class ParkingImageController {

    private final ParkingImageService parkingImageService;

    
    @PostMapping("/{parkingId}/upload")
    public ResponseEntity<ParkingImage> uploadImage(
            @PathVariable Integer parkingId,
            @RequestBody ImageUploadRequest request) {
        try {
            ParkingImage savedImage = parkingImageService.uploadAndSaveImage(parkingId, request);
            return ResponseEntity.ok(savedImage);
        } catch (RuntimeException e) {
            System.err.println("Erreur: Erreur upload: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    
    @PostMapping("/{parkingId}")
    public ResponseEntity<ParkingImage> saveParkingImage(
            @PathVariable Integer parkingId,
            @RequestBody ParkingImageDTO imageDTO) {
        try {
            ParkingImage savedImage = parkingImageService.saveParkingImage(parkingId, imageDTO);
            return ResponseEntity.ok(savedImage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    
    @GetMapping("/{parkingId}")
    public ResponseEntity<List<ParkingImage>> getParkingImages(@PathVariable Integer parkingId) {
        List<ParkingImage> images = parkingImageService.getImagesByParkingId(parkingId);
        return ResponseEntity.ok(images);
    }

    
    @DeleteMapping("/{parkingId}/file")
    public ResponseEntity<Void> deleteParkingImage(
            @PathVariable Integer parkingId,
            @RequestParam String filePath) {
        try {
            parkingImageService.deleteParkingImage(parkingId, filePath);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
