package com.urban.upark.repositories;

import com.urban.upark.models.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    
    // Rechercher les litiges d'un utilisateur (via ses réservations)
    @Query("SELECT d FROM Dispute d WHERE d.reservation.user.Id_Users = :userId ORDER BY d.createdAt DESC")
    List<Dispute> findByUserId(@Param("userId") Long userId);
    
    // Rechercher les litiges d'une réservation spécifique
    @Query("SELECT d FROM Dispute d WHERE d.reservation.Id_Reservation = :reservationId ORDER BY d.createdAt DESC")
    List<Dispute> findByReservationId(@Param("reservationId") Long reservationId);
}
