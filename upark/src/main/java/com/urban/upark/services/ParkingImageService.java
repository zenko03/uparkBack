package com.urban.upark.services;

import com.urban.upark.dto.parking.ImageUploadRequest;
import com.urban.upark.dto.parking.ParkingImageDTO;
import com.urban.upark.models.ParkingImage;
import com.urban.upark.repositories.ParkingImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingImageService {

    private final ParkingImageRepository parkingImageRepository;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public ParkingImage saveParkingImage(Integer parkingId, ParkingImageDTO imageDTO) {
        ParkingImage image = ParkingImage.builder()
                .idParking(parkingId)
                .filePath(imageDTO.getFilePath())
                .fileUrl(imageDTO.getFileUrl())
                .fileSize(imageDTO.getFileSize())
                .isPrimary(imageDTO.getIsPrimary() != null ? imageDTO.getIsPrimary() : false)
                .build();
        
        return parkingImageRepository.save(image);
    }

    public List<ParkingImage> getImagesByParkingId(Integer parkingId) {
        return parkingImageRepository.findByIdParking(parkingId);
    }

    @Transactional
    public void deleteParkingImage(Integer parkingId, String filePath) {
        parkingImageRepository.deleteByIdParkingAndFilePath(parkingId, filePath);
    }

    /**
     * Upload une image en base64 vers Supabase et sauvegarder les métadonnées
     */
    @Transactional
    public ParkingImage uploadAndSaveImage(Integer parkingId, ImageUploadRequest request) {
        try {
            System.out.println("📤 Upload image pour parking " + parkingId + " - User: " + request.getUserId());
            
            // Générer le nom de fichier avec chemin
            long timestamp = System.currentTimeMillis();
            String extension = request.getFileName().substring(request.getFileName().lastIndexOf('.'));
            String fileName = request.getUserId() + "/" + parkingId + "/" + timestamp + extension;
            
            // Upload vers Supabase Storage
            String publicUrl = supabaseStorageService.uploadFile(request.getImageBase64(), fileName);
            
            // Sauvegarder les métadonnées en BDD
            ParkingImage image = ParkingImage.builder()
                    .idParking(parkingId)
                    .filePath(fileName)
                    .fileUrl(publicUrl)
                    .fileSize(request.getImageBase64().length())
                    .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                    .build();
            
            ParkingImage savedImage = parkingImageRepository.save(image);
            System.out.println("✅ Image sauvegardée - ID: " + savedImage.getIdParkingImage());
            
            return savedImage;
        } catch (Exception e) {
            System.err.println("❌ Erreur upload image: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }
}
