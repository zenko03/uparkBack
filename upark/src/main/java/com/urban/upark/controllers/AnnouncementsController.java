package com.urban.upark.controllers;

import com.urban.upark.models.Announcements;
import com.urban.upark.services.AnnouncementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementsController {

    private final AnnouncementsService announcementsService;

    @GetMapping
    public List<Announcements> getAllAnnouncements() {
        return announcementsService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcements> getAnnouncementById(@PathVariable int id) {
        return announcementsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parking/{parkingId}")
    public List<Announcements> getAnnouncementsByParkingId(@PathVariable int parkingId) {
        return announcementsService.findByParkingId(parkingId);
    }

    @PostMapping
    public Announcements createAnnouncement(@RequestBody Announcements announcement) {
        if (announcement.getCreationDate() == null) {
            announcement.setCreationDate(LocalDateTime.now());
        }
        return announcementsService.save(announcement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Announcements> updateAnnouncement(@PathVariable int id, @RequestBody Announcements announcementDetails) {
        return announcementsService.findById(id)
                .map(announcement -> {
                    announcement.setDescription(announcementDetails.getDescription());
                    announcement.setPublished(announcementDetails.isPublished());
                    announcement.setParking(announcementDetails.getParking());
                    return ResponseEntity.ok(announcementsService.save(announcement));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/toggle-published")
    public ResponseEntity<Announcements> togglePublished(@PathVariable int id) {
        try {
            return ResponseEntity.ok(announcementsService.togglePublished(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable int id) {
        if (announcementsService.findById(id).isPresent()) {
            announcementsService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
