package com.urban.upark.repositories;

import com.urban.upark.models.ParkingVehicles;
import com.urban.upark.models.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingVehiclesRepository extends JpaRepository<ParkingVehicles, Integer> {
    List<ParkingVehicles> findByParkingId(int parkingId);
}