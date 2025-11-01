package com.urban.upark.services;

import com.urban.upark.models.ParkingNote;
import com.urban.upark.repositories.ParkingNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingNoteService {

    private final ParkingNoteRepository parkingNoteRepository;

    public List<ParkingNote> findAll() {
        return parkingNoteRepository.findAll();
    }

    public Optional<ParkingNote> findById(int id) {
        return parkingNoteRepository.findById(id);
    }

    public ParkingNote save(ParkingNote parkingNote) {
        return parkingNoteRepository.save(parkingNote);
    }

    public void deleteById(int id) {
        parkingNoteRepository.deleteById(id);
    }
}