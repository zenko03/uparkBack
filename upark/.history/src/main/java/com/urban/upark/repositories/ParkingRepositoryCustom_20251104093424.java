package com.urban.upark.repositories;

import com.urban.upark.models.Parking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ParkingRepositoryCustom {
    
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO parking (label, hourly_rate, description, localisation, id_users) " +
                   "VALUES (:label, :hourlyRate, :description, ST_GeogFromText(:localisation), :userId)", 
           nativeQuery = true)
    void insertParkingWithGeography(@Param("label") String label, 
                                   @Param("hourlyRate") java.math.BigDecimal hourlyRate,
                                   @Param("description") String description,
                                   @Param("localisation") String localisation,
                                   @Param("userId") Integer userId);
}