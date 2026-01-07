package com.urban.upark.services;

import com.urban.upark.models.GlobalParkingNote;
import com.urban.upark.repositories.GlobalParkingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalParkingNoteService {

    private final GlobalParkingNoteRepository globalParkingNoteRepository;

    public List<GlobalParkingNote> findAll() {
        return globalParkingNoteRepository.findAll();
    }
}
