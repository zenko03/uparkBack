package com.urban.upark.repositories;

import com.urban.upark.models.ParkingVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingVehiclesRepository extends JpaRepository<ParkingVehicles, Integer> {
    @Query("SELECT pv FROM ParkingVehicles pv WHERE pv.parking.Id_Parking = :parkingId")
    List<ParkingVehicles> findByParkingId(@Param("parkingId") int parkingId);
}