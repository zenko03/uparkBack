package com.urban.upark.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Days_week")
public class DaysWeek {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Days_week;

    @Column(name = "day_name", nullable = false, length = 50)
    private String dayName;
    
    @OneToMany(mappedBy = "dayOfWeek")
    @JsonIgnore
    private List<AvailabilitiesFrequence> availabilitiesFrequences;
}