package com.urban.upark.controllers;

import com.urban.upark.models.ParkingNote;
import com.urban.upark.services.ParkingNoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parking-notes")
@RequiredArgsConstructor
public class ParkingNoteController {

    private final ParkingNoteService parkingNoteService;

    @GetMapping
    public List<ParkingNote> getAllParkingNotes() {
        return parkingNoteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingNote> getParkingNoteById(@PathVariable int id) {
        Optional<ParkingNote> parkingNote = parkingNoteService.findById(id);
        if (parkingNote.isPresent()) {
            return ResponseEntity.ok(parkingNote.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ParkingNote createParkingNote(@RequestBody ParkingNote parkingNote) {
        return parkingNoteService.save(parkingNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingNote> updateParkingNote(@PathVariable int id, @RequestBody ParkingNote parkingNoteDetails) {
        Optional<ParkingNote> parkingNote = parkingNoteService.findById(id);
        if (parkingNote.isPresent()) {
            ParkingNote updatedParkingNote = parkingNote.get();
            updatedParkingNote.setNote(parkingNoteDetails.getNote());
            updatedParkingNote.setUser(parkingNoteDetails.getUser());
            updatedParkingNote.setParking(parkingNoteDetails.getParking());
            return ResponseEntity.ok(parkingNoteService.save(updatedParkingNote));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingNote(@PathVariable int id) {
        Optional<ParkingNote> parkingNote = parkingNoteService.findById(id);
        if (parkingNote.isPresent()) {
            parkingNoteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}