package com.urban.upark.dto;

import com.urban.upark.models.Announcements;
import com.urban.upark.models.Parking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les annonces avec la note moyenne du parking incluse
 * Utilisé pour les endpoints qui nécessitent d'afficher la note sans requête supplémentaire
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementWithRatingDTO {
    
    private int id_Announcements;
    private String description;
    private LocalDateTime creationDate;
    private boolean isPublished;
    private Parking parking;
    private Double averageRating;  // Note moyenne du parking (peut être null si pas de notes)
    
    /**
     * Constructeur à partir d'une entité Announcements
     */
    public static AnnouncementWithRatingDTO fromEntity(Announcements announcement, Double averageRating) {
        return AnnouncementWithRatingDTO.builder()
                .id_Announcements(announcement.getId_Announcements())
                .description(announcement.getDescription())
                .creationDate(announcement.getCreationDate())
                .isPublished(announcement.isPublished())
                .parking(announcement.getParking())
                .averageRating(averageRating)
                .build();
    }
}
