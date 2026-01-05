package com.urban.upark.repositories;

import com.urban.upark.models.AvailabilitiesDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilitiesDateRepository extends JpaRepository<AvailabilitiesDate, Integer> {
    
    @Query("SELECT a FROM AvailabilitiesDate a " +
           "WHERE a.announcementsVehicles.parkingVehicles.parking.Id_Parking = :parkingId " +
           "AND ((a.startDate <= :endDate AND a.endDate >= :startDate))")
    List<AvailabilitiesDate> findOverlappingAvailabilities(
        @Param("parkingId") int parkingId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT ad FROM AvailabilitiesDate ad WHERE ad.announcementsVehicles.Id_Announcements_vehicles = :announcementVehicleId")
    List<AvailabilitiesDate> findByAnnouncementVehicleId(@Param("announcementVehicleId") int announcementVehicleId);
    
    @Modifying
    @Query("DELETE FROM AvailabilitiesDate ad WHERE ad.announcementsVehicles.Id_Announcements_vehicles = :announcementVehicleId")
    void deleteByAnnouncementVehicleId(@Param("announcementVehicleId") int announcementVehicleId);
}