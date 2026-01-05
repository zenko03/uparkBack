package com.urban.upark.repositories;

import com.urban.upark.models.AnnouncementsVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementsVehiclesRepository extends JpaRepository<AnnouncementsVehicles, Integer> {
    
    @Query("SELECT av FROM AnnouncementsVehicles av " +
           "JOIN FETCH av.parkingVehicles pv " +
           "JOIN FETCH pv.parking p " +
           "JOIN FETCH pv.vehicle v " +
           "WHERE p.Id_Parking = :parkingId " +
           "AND v.Id_Vehicles = :vehicleTypeId")
    List<AnnouncementsVehicles> findByParkingAndVehicleType(
        @Param("parkingId") int parkingId, 
        @Param("vehicleTypeId") int vehicleTypeId
    );
    
    // Alternative avec requête SQL native
    @Query(value = "SELECT av.* FROM announcements_vehicles av " +
                   "JOIN parking_vehicles pv ON av.id_parking_vehicles = pv.id_parking_vehicles " +
                   "WHERE pv.id_parking = :parkingId " +
                   "AND pv.id_vehicles = :vehicleTypeId " +
                   "LIMIT 1", 
           nativeQuery = true)
    AnnouncementsVehicles findByParkingAndVehicleTypeNative(
        @Param("parkingId") int parkingId, 
        @Param("vehicleTypeId") int vehicleTypeId
    );
    
    @Query("SELECT av FROM AnnouncementsVehicles av WHERE av.announcements.Id_Announcements = :announcementId")
    List<AnnouncementsVehicles> findByAnnouncementId(@Param("announcementId") int announcementId);
    
    @Query("SELECT av FROM AnnouncementsVehicles av " +
           "JOIN FETCH av.parkingVehicles pv " +
           "JOIN FETCH pv.vehicle v " +
           "WHERE av.announcements.Id_Announcements = :announcementId " +
           "AND v.Id_Vehicles = :vehicleTypeId")
    AnnouncementsVehicles findByAnnouncementIdAndVehicleTypeId(
        @Param("announcementId") int announcementId, 
        @Param("vehicleTypeId") int vehicleTypeId
    );
    
    @Modifying
    @Query("DELETE FROM AnnouncementsVehicles av WHERE av.announcements.Id_Announcements = :announcementId")
    void deleteByAnnouncementId(@Param("announcementId") int announcementId);
}