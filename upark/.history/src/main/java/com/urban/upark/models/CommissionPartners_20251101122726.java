package com.urban.upark.models;

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
@Table(name = "Commission_partners")
public class CommissionPartners {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Commission_partners;

    private Double rate;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "Id_Users")
    private Users user;
}