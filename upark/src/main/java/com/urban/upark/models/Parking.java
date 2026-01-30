package com.urban.upark.models;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Parking")
public class Parking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Id_Parking")
    private int Id_Parking;

    @Column(nullable = false)
    private String label;

    @Column(name = "hourly_rate", nullable = false, precision = 15, scale = 2)
    private BigDecimal hourlyRate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "localisation", nullable = false, columnDefinition = "GEOGRAPHY")
    private String localisation;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @JsonProperty("isActive")
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @JsonProperty("isDeleted")
    private boolean isDeleted = false;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "Id_Users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "commissionPartners"})
    private Users user;
    
    @OneToMany(mappedBy = "parking")
    @JsonIgnore
    private List<ParkingVehicles> parkingVehicles;
    
    
     // Relation avec les annonces du parking
     
    @OneToMany(mappedBy = "parking", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"parking", "announcementsVehicles"})
    private List<Announcements> announcements;
    
   
     //Relation avec les images du parking
     
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parking", referencedColumnName = "Id_Parking")
    @JsonProperty("images")
    private List<ParkingImage> images;
    
    /**
     * Extrait la latitude depuis le champ GEOGRAPHY en utilisant PostGIS ST_Y
     */
    @org.hibernate.annotations.Formula("ST_Y(localisation::geometry)")
    @JsonProperty("latitude")
    private Double latitude;
    
    /**
     * Extrait la longitude depuis le champ GEOGRAPHY en utilisant PostGIS ST_X
     */
    @org.hibernate.annotations.Formula("ST_X(localisation::geometry)")
    @JsonProperty("longitude")
    private Double longitude;
    
   
    @Transient
    @JsonProperty("primaryImageUrl")
    public String getPrimaryImageUrl() {
        if (images == null || images.isEmpty()) {
            return null;
        }
        
        // Chercher l'image marquée comme principale
        return images.stream()
            .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
            .findFirst()
            .map(ParkingImage::getFileUrl)
            .orElseGet(() -> {
                // Fallback: retourner la première image si aucune n'est marquée comme principale
                return images.get(0).getFileUrl();
            });
    }
    
    /**
     * Retourne l'ID de l'annonce active/publiée pour ce parking
     * Si aucune annonce publiée, retourne null
     */
    @Transient
    @JsonProperty("Id_Announcements")
    public Integer getIdAnnouncements() {
        if (announcements == null || announcements.isEmpty()) {
            return null;
        }
        
        // Retourner l'annonce publiée
        return announcements.stream()
            .filter(Announcements::isPublished)
            .findFirst()
            .map(Announcements::getId_Announcements)
            .orElse(null);
    }
    
    /**
     * Suppression logique du parking
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    /**
     * Restauration d'un parking supprimé
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
