package com.urban.upark.services;

import com.urban.upark.models.Announcements;
import com.urban.upark.repositories.AnnouncementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementsService {

    private final AnnouncementsRepository announcementsRepository;

    public List<Announcements> findAll() {
        return announcementsRepository.findAll();
    }

    public Optional<Announcements> findById(int id) {
        return announcementsRepository.findById(id);
    }

    public List<Announcements> findByParkingId(int parkingId) {
        return announcementsRepository.findByParkingId(parkingId);
    }

    @Transactional
    public Announcements save(Announcements announcement) {
        return announcementsRepository.save(announcement);
    }

    @Transactional
    public void deleteById(int id) {
        announcementsRepository.deleteById(id);
    }

    @Transactional
    public Announcements togglePublished(int id) {
        Announcements announcement = announcementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcement.setPublished(!announcement.isPublished());
        return announcementsRepository.save(announcement);
    }
}
