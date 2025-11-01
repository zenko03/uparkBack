package com.urban.upark.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehicles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Vehicles;

    private String types;

    private String icon;
}