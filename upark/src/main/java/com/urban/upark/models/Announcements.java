package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Announcements")
public class Announcements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Announcements;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private boolean isPublished = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Id_Parking")
    private Parking parking;
    
    @OneToMany(mappedBy = "announcements")
    @JsonIgnore
    private List<AnnouncementsVehicles> announcementsVehicles;
}