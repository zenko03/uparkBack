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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Parking")
public class Parking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking;

    @Column(nullable = false)
    private String label;

    @Column(name = "hourly_rate", nullable = false, precision = 15, scale = 2)
    private BigDecimal hourlyRate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "localisation", nullable = false, columnDefinition = "GEOGRAPHY")
    private String localisation;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "Id_Users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "commissionPartners"})
    private Users user;
    
    @OneToMany(mappedBy = "parking")
    @JsonIgnore
    private List<ParkingVehicles> parkingVehicles;
}