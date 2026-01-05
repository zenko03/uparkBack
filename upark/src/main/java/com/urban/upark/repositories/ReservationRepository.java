package com.urban.upark.repositories;

import com.urban.upark.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    // cherche les réservations qui se chevauchent pour un parking donné:
    // oh: startDate1 <= endDate2 AND endDate1 >= startDate2
    @Query("SELECT r FROM Reservation r JOIN r.reservationVehicles rv JOIN rv.announcementsVehicles av JOIN av.availabilitiesDates ad WHERE ad.announcementsVehicles.parkingVehicles.parking.Id_Parking = :parkingId AND " +
           "((ad.startDate <= :endDate AND ad.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
        @Param("parkingId") int parkingId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT r FROM Reservation r WHERE r.user.Id_Users = :userId ORDER BY r.creationDate DESC")
    List<Reservation> findByUserIdOrderByCreationDateDesc(@Param("userId") int userId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
           "LEFT JOIN FETCH r.reservationVehicles rv " +
           "LEFT JOIN FETCH rv.announcementsVehicles av " +
           "LEFT JOIN FETCH av.parkingVehicles pv " +
           "LEFT JOIN FETCH pv.parking " +
           "WHERE r.user.Id_Users = :userId " +
           "ORDER BY r.creationDate DESC")
    List<Reservation> findByUserIdWithParkingInfo(@Param("userId") int userId);

    // Version simplifiée sans les paramètres de date pour éviter les problèmes de type
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:statusId IS NULL OR r.reservationStatus.Id_Reservation_status = :statusId) AND " +
           "(:userId IS NULL OR r.user.Id_Users = :userId) AND " +
           "(:parkingId IS NULL OR EXISTS (" +
           "    SELECT 1 FROM ReservationVehicles rv " +
           "    JOIN rv.announcementsVehicles av " +
           "    WHERE rv.reservation.Id_Reservation = r.Id_Reservation " +
           "    AND av.parkingVehicles.parking.Id_Parking = :parkingId" +
           ")) " +
           "ORDER BY r.creationDate DESC")
    List<Reservation> findReservationsWithBasicFilters(
        @Param("statusId") Integer statusId,
        @Param("userId") Integer userId,
        @Param("parkingId") Integer parkingId
    );

    // Version avec dates - utilisation de COALESCE pour éviter les problèmes de typage
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:statusId IS NULL OR r.reservationStatus.Id_Reservation_status = :statusId) AND " +
           "(:userId IS NULL OR r.user.Id_Users = :userId) AND " +
           "(:parkingId IS NULL OR EXISTS (" +
           "    SELECT 1 FROM ReservationVehicles rv " +
           "    JOIN rv.announcementsVehicles av " +
           "    WHERE rv.reservation.Id_Reservation = r.Id_Reservation " +
           "    AND av.parkingVehicles.parking.Id_Parking = :parkingId" +
           ")) AND " +
           "(COALESCE(:startDate, '1970-01-01T00:00:00') IS NULL OR r.creationDate >= :startDate) AND " +
           "(COALESCE(:endDate, '2999-12-31T23:59:59') IS NULL OR r.creationDate <= :endDate) " +
           "ORDER BY r.creationDate DESC")
    List<Reservation> findReservationsWithDateFilters(
        @Param("statusId") Integer statusId,
        @Param("userId") Integer userId,
        @Param("parkingId") Integer parkingId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Version simplifiée pour by-date-range avec COALESCE
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:statusId IS NULL OR r.reservationStatus.Id_Reservation_status = :statusId) AND " +
           "(COALESCE(:startDate, '1970-01-01T00:00:00') IS NULL OR r.startDateTime >= :startDate) AND " +
           "(COALESCE(:endDate, '2999-12-31T23:59:59') IS NULL OR r.endDateTime <= :endDate) " +
           "ORDER BY r.startDateTime DESC")
    List<Reservation> findReservationsByDateRange(
        @Param("statusId") Integer statusId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Trouver les réservations avec un statut spécifique et date de début passée
    @Query("SELECT r FROM Reservation r WHERE r.reservationStatus.Id_Reservation_status = :statusId AND r.startDateTime < :dateTime")
    List<Reservation> findByReservationStatusIdAndStartDateTimeBefore(
        @Param("statusId") Integer statusId,
        @Param("dateTime") LocalDateTime dateTime
    );
    
    // Trouver les réservations avec un statut spécifique et date de fin passée
    @Query("SELECT r FROM Reservation r WHERE r.reservationStatus.Id_Reservation_status = :statusId AND r.endDateTime < :dateTime")
    List<Reservation> findByReservationStatusIdAndEndDateTimeBefore(
        @Param("statusId") Integer statusId,
        @Param("dateTime") LocalDateTime dateTime
    );
    
    // Trouver les réservations par statut
    @Query("SELECT r FROM Reservation r WHERE r.reservationStatus.Id_Reservation_status = :statusId")
    List<Reservation> findByReservationStatusId(@Param("statusId") Integer statusId);
    
    /**
     * Récupérer les réservations d'un utilisateur avec les informations du parking
     * Utilise la vue SQL v_user_reservations pour des performances optimales
     */
    @Query(value = "SELECT * FROM v_user_reservations WHERE id_users = :userId ORDER BY creation_date DESC", nativeQuery = true)
    List<Map<String, Object>> findUserReservationsFromView(@Param("userId") int userId);
}