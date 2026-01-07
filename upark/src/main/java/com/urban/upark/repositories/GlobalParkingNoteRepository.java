package com.urban.upark.repositories;

import com.urban.upark.models.GlobalParkingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalParkingNoteRepository extends JpaRepository<GlobalParkingNote, Integer> {
}
