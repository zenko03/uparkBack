package com.urban.upark.services;

import com.urban.upark.models.GlobalParkingNote;
import com.urban.upark.models.GlobalUserNote;
import com.urban.upark.models.UserNote;
import com.urban.upark.repositories.GlobalParkingNoteRepository;
import com.urban.upark.repositories.GlobalUserNoteRepository;
import com.urban.upark.repositories.UserNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserNoteService {

    private final UserNoteRepository userNoteRepository;
    private final GlobalParkingNoteRepository globalParkingNoteRepository;
    private final GlobalUserNoteRepository globalUserNoteRepository;

    public UserNote save(UserNote userNote) {
        return userNoteRepository.save(userNote);
    }

    public List<UserNote> findAll() {
        return userNoteRepository.findAll();
    }

    public Optional<UserNote> findById(Long id) {
        return userNoteRepository.findById(id);
    }

    public void deleteById(Long id) {
        userNoteRepository.deleteById(id);
    }

    public List<UserNote> findByUserId(int userId) {
        return userNoteRepository.findByUserId(userId);
    }

    public List<UserNote> findByParkingId(int parkingId) {
        return userNoteRepository.findByParkingId(parkingId);
    }

    public List<GlobalParkingNote> findAllParkingAverages() {
        return globalParkingNoteRepository.findAll();
    }

    public Optional<GlobalParkingNote> findParkingAverage(int parkingId) {
        return globalParkingNoteRepository.findById(parkingId);
    }

    public List<GlobalUserNote> findAllUserAverages() {
        return globalUserNoteRepository.findAll();
    }

    public Optional<GlobalUserNote> findUserAverage(int userId) {
        return globalUserNoteRepository.findById(userId);
    }
}
