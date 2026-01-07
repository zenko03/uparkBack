package com.urban.upark.controllers;

import com.urban.upark.models.GlobalParkingNote;
import com.urban.upark.services.GlobalParkingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/global-parking-notes")
@RequiredArgsConstructor
public class GlobalParkingNoteController {

    private final GlobalParkingNoteService globalParkingNoteService;

    @GetMapping
    public ResponseEntity<List<GlobalParkingNote>> getAllGlobalParkingNotes() {
        return ResponseEntity.ok(globalParkingNoteService.findAll());
    }
}
