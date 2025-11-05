package com.urban.upark.models;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Availabilities_date")
public class AvailabilitiesDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Availabilities_date;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Reservation_vehicles")
    private ReservationVehicles reservationVehicles;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Announcements_vehicles")
    private AnnouncementsVehicles announcementsVehicles;
}