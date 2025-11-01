package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Commission_types")
public class CommissionTypes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Commission_types;

    private String label;
}