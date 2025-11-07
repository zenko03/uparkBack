package com.urban.upark.repositories;

import com.urban.upark.models.AnnouncementsVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementsVehiclesRepository extends JpaRepository<AnnouncementsVehicles, Integer> {
    
    @Query("SELECT av FROM AnnouncementsVehicles av " +
           "WHERE av.parkingVehicles.parking.Id_Parking = :parkingId " +
           "AND av.parkingVehicles.vehicle.Id_Vehicles = :vehicleTypeId")
    List<AnnouncementsVehicles> findByParkingAndVehicleType(
        @Param("parkingId") int parkingId, 
        @Param("vehicleTypeId") int vehicleTypeId
    );
}