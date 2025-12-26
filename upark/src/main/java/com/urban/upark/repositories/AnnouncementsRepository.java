package com.urban.upark.repositories;

import com.urban.upark.models.Announcements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementsRepository extends JpaRepository<Announcements, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Announcements a WHERE a.parking.Id_Parking = :parkingId")
    List<Announcements> findByParkingId(@Param("parkingId") int parkingId);
}
