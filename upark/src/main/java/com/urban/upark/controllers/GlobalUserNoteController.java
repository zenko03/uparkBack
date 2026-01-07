package com.urban.upark.controllers;

import com.urban.upark.models.GlobalUserNote;
import com.urban.upark.services.GlobalUserNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/global-user-notes")
@RequiredArgsConstructor
public class GlobalUserNoteController {

    private final GlobalUserNoteService globalUserNoteService;

    @GetMapping
    public ResponseEntity<List<GlobalUserNote>> getAllGlobalUserNotes() {
        return ResponseEntity.ok(globalUserNoteService.findAll());
    }
}
