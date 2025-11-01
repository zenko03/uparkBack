package com.urban.upark.repositories;

import com.urban.upark.models.ParkingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingNoteRepository extends JpaRepository<ParkingNote, Integer> {
}