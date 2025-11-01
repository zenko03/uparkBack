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
@Table(name = "Reservation_delay")
public class ReservationDelay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Reservation_delay;

    @Column(name = "delay_in_hours")
    private Integer delayInHours;

    @Column(name = "delay_in_minutes")
    private Integer delayInMinutes;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}