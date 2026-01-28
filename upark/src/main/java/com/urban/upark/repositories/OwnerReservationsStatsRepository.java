package com.urban.upark.repositories;

import com.urban.upark.models.OwnerReservationsStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerReservationsStatsRepository extends JpaRepository<OwnerReservationsStats, Integer> {
    Optional<OwnerReservationsStats> findByOwnerId(Integer ownerId);
}
