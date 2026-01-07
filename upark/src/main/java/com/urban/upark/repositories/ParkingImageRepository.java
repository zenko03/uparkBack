package com.urban.upark.repositories;

import com.urban.upark.models.ParkingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingImageRepository extends JpaRepository<ParkingImage, Integer> {
    List<ParkingImage> findByIdParking(Integer idParking);
    void deleteByIdParkingAndFilePath(Integer idParking, String filePath);
}
