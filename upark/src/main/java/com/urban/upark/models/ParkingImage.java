package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parking_images")
public class ParkingImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parking_image")
    @JsonProperty("idParkingImage")
    private Integer idParkingImage;

    @Column(name = "id_parking", nullable = false)
    @JsonProperty("idParking")
    private Integer idParking;

    @Column(name = "file_path", nullable = false)
    @JsonProperty("filePath")
    private String filePath;

    @Column(name = "file_url", nullable = false)
    @JsonProperty("fileUrl")
    private String fileUrl;

    @Column(name = "file_size")
    @JsonProperty("fileSize")
    private Integer fileSize;

    @Column(name = "is_primary")
    @Builder.Default
    @JsonProperty("isPrimary")
    private Boolean isPrimary = false;

    @Column(name = "created_at", updatable = false)
    @JsonProperty("createdAt")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parking", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parkingVehicles", "user"})
    private Parking parking;
}
