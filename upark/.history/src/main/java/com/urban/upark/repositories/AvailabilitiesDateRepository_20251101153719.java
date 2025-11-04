package com.urban.upark.repositories;

import com.urban.upark.models.AvailabilitiesDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilitiesDateRepository extends JpaRepository<AvailabilitiesDate, Integer> {
    
    @Query("SELECT a FROM AvailabilitiesDate a WHERE a.parking.Id_Parking = :parkingId AND " +
           "((a.start_date <= :endDate AND a.end_date >= :startDate))")
    List<AvailabilitiesDate> findOverlappingAvailabilities(
        @Param("parkingId") int parkingId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}