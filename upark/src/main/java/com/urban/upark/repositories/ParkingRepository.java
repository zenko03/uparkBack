package com.urban.upark.repositories;

import com.urban.upark.models.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Integer> {
    
    @Query(value = "SELECT p.*, ST_AsText(p.localisation) as localisation " +
           "FROM parking p",
           nativeQuery = true)
    List<Parking> findAllWithLocationText();
    
    @Query(value = "SELECT p.*, ST_AsText(p.localisation) as localisation " +
           "FROM parking p WHERE p.id_parking = :id",
           nativeQuery = true)
    Parking findByIdWithLocationText(@Param("id") int id);
    
    @Query(value = "SELECT *, " +
           "ST_Distance(localisation, ST_GeogFromText(:location)) as distance " +
           "FROM parking " +
           "WHERE ST_DWithin(localisation, ST_GeogFromText(:location), :radius) " +
           "ORDER BY distance",
           nativeQuery = true)
    List<Parking> findParkingsByLocation(
        @Param("location") String location,
        @Param("radius") double radius
    );
    
    @Query("SELECT p FROM Parking p WHERE " +
           "LOWER(p.label) LIKE LOWER(CONCAT('%', :address, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<Parking> findByAddressContaining(@Param("address") String address);
}