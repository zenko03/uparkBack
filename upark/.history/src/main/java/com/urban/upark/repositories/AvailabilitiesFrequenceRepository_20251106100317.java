package com.urban.upark.repositories;

import com.urban.upark.models.AvailabilitiesFrequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitiesFrequenceRepository extends JpaRepository<AvailabilitiesFrequence, Integer> {
    
    @Query("SELECT af FROM AvailabilitiesFrequence af " +
           "WHERE af.announcementsVehicles.parkingVehicles.parking.Id_Parking = :parkingId " +
           "AND af.dayOfWeek.id = :dayOfWeekId")
    List<AvailabilitiesFrequence> findByParkingAndDayOfWeek(
        @Param("parkingId") int parkingId, 
        @Param("dayOfWeekId") int dayOfWeekId
    );
}