package com.urban.upark.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Reservation_vehicles")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ReservationVehicles reservationVehicles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Announcements_vehicles")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AnnouncementsVehicles announcementsVehicles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Days_week")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DaysWeek dayOfWeek;
}