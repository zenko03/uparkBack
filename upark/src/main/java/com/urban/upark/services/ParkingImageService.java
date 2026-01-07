package com.urban.upark.services;

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
}
