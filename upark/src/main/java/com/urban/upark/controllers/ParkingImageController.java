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

    /**
     * Upload une image en base64 vers Supabase et sauvegarder les métadonnées
     * @param parkingId ID du parking
     * @param request Requête avec image base64
     * @return Image uploadée et sauvegardée
     */
    @PostMapping("/{parkingId}/upload")
    public ResponseEntity<ParkingImage> uploadImage(
            @PathVariable Integer parkingId,
            @RequestBody ImageUploadRequest request) {
        try {
            ParkingImage savedImage = parkingImageService.uploadAndSaveImage(parkingId, request);
            return ResponseEntity.ok(savedImage);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur upload: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Sauvegarder une image de parking
     * @param parkingId ID du parking
     * @param imageDTO Données de l'image (filePath, fileUrl, fileSize, isPrimary)
     * @return Image sauvegardée
     */
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

    /**
     * Récupérer toutes les images d'un parking
     * @param parkingId ID du parking
     * @return Liste des images
     */
    @GetMapping("/{parkingId}")
    public ResponseEntity<List<ParkingImage>> getParkingImages(@PathVariable Integer parkingId) {
        List<ParkingImage> images = parkingImageService.getImagesByParkingId(parkingId);
        return ResponseEntity.ok(images);
    }

    /**
     * Supprimer une image de parking
     * @param parkingId ID du parking
     * @param filePath Chemin du fichier à supprimer
     * @return 204 No Content
     */
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
