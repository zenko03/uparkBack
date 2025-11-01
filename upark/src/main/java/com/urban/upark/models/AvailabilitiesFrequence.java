package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Availabilities_frequence")
public class AvailabilitiesFrequence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Availabilities_frequence;

    @Column(name = "start_hour")
    private LocalTime startHour;

    @Column(name = "end_hour")
    private LocalTime endHour;

    @ManyToOne
    @JoinColumn(name = "Id_Days_week")
    private DaysWeek dayOfWeek;
}