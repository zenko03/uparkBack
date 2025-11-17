package com.urban.upark.repositories;

import com.urban.upark.models.CommissionReceived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommissionReceivedRepository extends JpaRepository<CommissionReceived, Integer> {
    @Query("SELECT c FROM CommissionReceived c WHERE c.reservation.Id_Reservation = :reservationId")
    List<CommissionReceived> findByReservationId(@Param("reservationId") int reservationId);
}