package com.urban.upark.repositories;

import com.urban.upark.models.ReservationRequestVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRequestVehiclesRepository extends JpaRepository<ReservationRequestVehicles, Integer> {
    
    /**
     * Récupérer tous les véhicules sélectionnés pour une demande
     */
    @Query("SELECT rrv FROM ReservationRequestVehicles rrv " +
           "JOIN FETCH rrv.announcementsVehicles av " +
           "JOIN FETCH av.parkingVehicles pv " +
           "JOIN FETCH pv.vehicle v " +
           "WHERE rrv.reservationRequest.id = :requestId")
    List<ReservationRequestVehicles> findByReservationRequestId(@Param("requestId") Long requestId);
    
    /**
     * Supprimer tous les véhicules d'une demande (si modification)
     */
    void deleteByReservationRequestId(Long requestId);
}
