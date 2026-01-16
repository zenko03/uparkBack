package com.urban.upark.controllers;

import com.urban.upark.models.GlobalParkingNote;
import com.urban.upark.models.GlobalUserNote;
import com.urban.upark.models.UserNote;
import com.urban.upark.services.UserNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-notes")
@RequiredArgsConstructor
public class UserNoteController {

    private final UserNoteService userNoteService;

    @PostMapping
    public ResponseEntity<UserNote> createNote(@RequestBody UserNote userNote) {
        return ResponseEntity.ok(userNoteService.save(userNote));
    }

    @GetMapping
    public ResponseEntity<List<UserNote>> getAllNotes() {
        return ResponseEntity.ok(userNoteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNote> getNoteById(@PathVariable Long id) {
        return userNoteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        userNoteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNote>> getNotesByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(userNoteService.findByUserId(userId));
    }

    @GetMapping("/parking/{parkingId}")
    public ResponseEntity<List<UserNote>> getNotesByParkingId(@PathVariable int parkingId) {
        return ResponseEntity.ok(userNoteService.findByParkingId(parkingId));
    }

    // Endpoints pour les statistiques (vues)
    @GetMapping("/statistics/parkings")
    public ResponseEntity<List<GlobalParkingNote>> getAllParkingAverages() {
        return ResponseEntity.ok(userNoteService.findAllParkingAverages());
    }

    @GetMapping("/statistics/parking/{parkingId}")
    public ResponseEntity<GlobalParkingNote> getParkingAverage(@PathVariable int parkingId) {
        return userNoteService.findParkingAverage(parkingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics/users")
    public ResponseEntity<List<GlobalUserNote>> getAllUserAverages() {
        return ResponseEntity.ok(userNoteService.findAllUserAverages());
    }

    @GetMapping("/statistics/user/{userId}")
    public ResponseEntity<GlobalUserNote> getUserAverage(@PathVariable int userId) {
        return userNoteService.findUserAverage(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
