package com.urban.upark.repositories;

import com.urban.upark.models.Announcements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementsRepository extends JpaRepository<Announcements, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Announcements a WHERE a.parking.Id_Parking = :parkingId")
    List<Announcements> findByParkingId(@Param("parkingId") int parkingId);
    
    List<Announcements> findByIsPublishedTrue();
    
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Announcements a WHERE a.parking.user.Id_Users = :userId")
    List<Announcements> findByUserId(@Param("userId") int userId);

    /**
     * Recherche avancée d'annonces publiées avec filtres optionnels
     * - searchText: recherche dans le nom du parking ou la description
     * - vehicleTypeId: filtre par type de véhicule disponible dans l'annonce
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT a FROM Announcements a " +
        "LEFT JOIN a.announcementsVehicles av " +
        "LEFT JOIN av.parkingVehicles pv " +
        "LEFT JOIN pv.vehicle v " +
        "WHERE a.isPublished = true " +
        "AND (:searchText IS NULL OR :searchText = '' OR " +
        "     LOWER(a.parking.label) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
        "     LOWER(a.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
        "     LOWER(a.parking.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
        "AND (:vehicleTypeId IS NULL OR v.Id_Vehicles = :vehicleTypeId) " +
        "AND (:minPlaces IS NULL OR av.numbers >= :minPlaces)"
    )
    List<Announcements> searchAnnouncements(
        @Param("searchText") String searchText,
        @Param("vehicleTypeId") Integer vehicleTypeId,
        @Param("minPlaces") Integer minPlaces
    );
}
