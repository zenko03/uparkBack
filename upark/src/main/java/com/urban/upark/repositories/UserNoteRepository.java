package com.urban.upark.repositories;

import com.urban.upark.models.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote, Long> {
    List<UserNote> findByUserId(int userId);
    List<UserNote> findByParkingId_Parking(int parkingId);
}
