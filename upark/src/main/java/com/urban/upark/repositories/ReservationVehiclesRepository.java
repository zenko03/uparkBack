package com.urban.upark.repositories;

import com.urban.upark.models.ReservationVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationVehiclesRepository extends JpaRepository<ReservationVehicles, Integer> {
    
    @Query("SELECT rv FROM ReservationVehicles rv WHERE rv.reservation.Id_Reservation = :reservationId")
    List<ReservationVehicles> findByReservationId(@Param("reservationId") int reservationId);
}