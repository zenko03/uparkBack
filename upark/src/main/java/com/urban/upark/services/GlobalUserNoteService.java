package com.urban.upark.services;

import com.urban.upark.models.GlobalUserNote;
import com.urban.upark.repositories.GlobalUserNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalUserNoteService {

    private final GlobalUserNoteRepository globalUserNoteRepository;

    public List<GlobalUserNote> findAll() {
        return globalUserNoteRepository.findAll();
    }
}
