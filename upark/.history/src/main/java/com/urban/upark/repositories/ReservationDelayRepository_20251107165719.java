package com.urban.upark.repositories;

import com.urban.upark.models.ReservationDelay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationDelayRepository extends JpaRepository<ReservationDelay, Integer> {
    
    // Récupérer le délai de réservation actif (le plus récent)
    Optional<ReservationDelay> findTopByOrderByCreationDateDesc();
    
    // Vérifier s'il existe un délai de configuration
    boolean existsById(int id);
}