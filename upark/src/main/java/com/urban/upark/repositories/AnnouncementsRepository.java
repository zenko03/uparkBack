package com.urban.upark.repositories;

import com.urban.upark.models.Announcements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnnouncementsRepository extends JpaRepository<Announcements, Integer> {
    @Query("SELECT a FROM Announcements a WHERE a.parking.Id_Parking = :parkingId AND a.isDeleted = false")
    List<Announcements> findByParkingId(@Param("parkingId") int parkingId);
    
    @Query("SELECT a FROM Announcements a WHERE a.isPublished = true AND a.isDeleted = false")
    List<Announcements> findByIsPublishedTrue();
    
    @Query("SELECT a FROM Announcements a WHERE a.parking.user.Id_Users = :userId AND a.isDeleted = false")
    List<Announcements> findByUserId(@Param("userId") int userId);

    /**
     * Recherche avancée d'annonces publiées avec filtres optionnels
     */
    @Query(
        "SELECT DISTINCT a FROM Announcements a " +
        "LEFT JOIN a.announcementsVehicles av " +
        "LEFT JOIN av.parkingVehicles pv " +
        "LEFT JOIN pv.vehicle v " +
        "WHERE a.isPublished = true " +
        "AND a.isDeleted = false " +
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
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE announcements SET is_deleted = true, deleted_at = NOW() WHERE id_announcements = :id", nativeQuery = true)
    void softDeleteById(@Param("id") int id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE announcements SET is_deleted = false, deleted_at = NULL WHERE id_announcements = :id", nativeQuery = true)
    void restoreById(@Param("id") int id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE announcements SET is_published = NOT is_published WHERE id_announcements = :id", nativeQuery = true)
    void togglePublishedById(@Param("id") int id);
    
    @Query("SELECT a FROM Announcements a WHERE a.isDeleted = false")
    List<Announcements> findAllActive();
}
