package com.urban.upark.repositories;

import com.urban.upark.models.ReservationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRequestRepository extends JpaRepository<ReservationRequest, Long> {
    
    // Demandes reçues par un propriétaire (via parking)
    @Query("SELECT rr FROM ReservationRequest rr WHERE rr.announcement.parking.user.Id_Users = :ownerId")
    List<ReservationRequest> findByAnnouncementParkingUserId(@Param("ownerId") Long ownerId);
    
    // Demandes envoyées par un client
    @Query("SELECT rr FROM ReservationRequest rr WHERE rr.requester.Id_Users = :requesterId")
    List<ReservationRequest> findByRequesterId(@Param("requesterId") Long requesterId);
    
    // Demandes par statut
    List<ReservationRequest> findByState(Short state);
    
    // Demandes d'une annonce spécifique
    @Query("SELECT rr FROM ReservationRequest rr WHERE rr.announcement.Id_Announcements = :announcementId")
    List<ReservationRequest> findByAnnouncementId(@Param("announcementId") Integer announcementId);
}
