package com.urban.upark.repositories;

import com.urban.upark.models.GlobalUserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalUserNoteRepository extends JpaRepository<GlobalUserNote, Integer> {
}
