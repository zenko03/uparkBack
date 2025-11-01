package com.urban.upark.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Parking;

    @Column(nullable = false)
    private String label;

    @Column(name = "hourly_rate", nullable = false, precision = 15, scale = 2)
    private Double hourlyRate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "localisation", nullable = false, columnDefinition = "GEOGRAPHY")
    private String localisation;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "Id_Users")
    private Users user;
}