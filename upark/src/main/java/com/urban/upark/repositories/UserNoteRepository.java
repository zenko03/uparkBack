package com.urban.upark.repositories;

import com.urban.upark.models.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote, Long> {
    @Query("SELECT un FROM UserNote un WHERE un.user.Id_Users = :userId")
    List<UserNote> findByUserId(@Param("userId") int userId);

    @Query("SELECT un FROM UserNote un WHERE un.parking.Id_Parking = :parkingId")
    List<UserNote> findByParkingId(@Param("parkingId") int parkingId);
}
