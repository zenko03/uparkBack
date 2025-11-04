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
    @Query("SELECT r FROM Reservation r WHERE r.parking.id_Parking = :parkingId AND " +
           "((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservations(
        @Param("parkingId") int parkingId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}