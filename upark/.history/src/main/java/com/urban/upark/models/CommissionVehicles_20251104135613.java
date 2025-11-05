package com.urban.upark.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "Commission_vehicles")
public class CommissionVehicles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Commission_vehicles;

    private Double rate;

    @Column(name = "creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "Id_Vehicles")
    private Vehicles vehicle;
}