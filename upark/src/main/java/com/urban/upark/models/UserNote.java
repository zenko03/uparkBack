package com.urban.upark.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_note")
public class UserNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "note")
    private Short note;

    @Column(name = "cleanliness")
    private Boolean cleanliness;

    @Column(name = "precision")
    private Boolean precision;

    @Column(name = "communication")
    private Boolean communication;

    @Column(name = "security")
    private Boolean security;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parking")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parkingVehicles", "user"})
    private Parking parking;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "commissionPartners", "reservations", "parkings", "role", "authorities"})
    private Users user;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
