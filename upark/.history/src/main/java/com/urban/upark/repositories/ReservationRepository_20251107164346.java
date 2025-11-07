package com.urban.upark.repositories;

import com.urban.upark.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    // Filtres avancés pour le back office
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:statusId IS NULL OR r.reservationStatus.Id_Reservation_status = :statusId) AND " +
           "(:userId IS NULL OR r.user.Id_Users = :userId) AND " +
           "(:parkingId IS NULL OR EXISTS (" +
           "    SELECT 1 FROM ReservationVehicles rv " +
           "    JOIN rv.announcementsVehicles av " +
           "    WHERE rv.reservation.Id_Reservation = r.Id_Reservation " +
           "    AND av.parkingVehicles.parking.Id_Parking = :parkingId" +
           ")) AND " +
           "(:startDate IS NULL OR r.creationDate >= :startDate) AND " +
           "(:endDate IS NULL OR r.creationDate <= :endDate) " +
           "ORDER BY r.creationDate DESC")
    List<Reservation> findReservationsWithFilters(
        @Param("statusId") Integer statusId,
        @Param("userId") Integer userId,
        @Param("parkingId") Integer parkingId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

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

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:statusId IS NULL OR r.reservationStatus.Id_Reservation_status = :statusId) AND " +
           "(:startDate IS NULL OR r.startDateTime >= :startDate) AND " +
           "(:endDate IS NULL OR r.endDateTime <= :endDate) " +
           "ORDER BY r.startDateTime DESC")
    List<Reservation> findReservationsByDateRange(
        @Param("statusId") Integer statusId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}