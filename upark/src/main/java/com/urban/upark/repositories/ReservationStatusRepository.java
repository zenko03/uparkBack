package com.urban.upark.repositories;

import com.urban.upark.models.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Integer> {
    
    @Query("SELECT rs FROM ReservationStatus rs WHERE rs.value = :value")
    Optional<ReservationStatus> findByValue(@Param("value") int value);
    
    @Query("SELECT rs FROM ReservationStatus rs WHERE rs.label = :label")
    Optional<ReservationStatus> findByLabel(@Param("label") String label);
}