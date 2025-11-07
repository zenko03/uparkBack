package com.urban.upark.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reservation_status")
public class ReservationStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Reservation_status;

    private String label;

    @Column(name = "value_")
    private Integer value;
    
    @OneToMany(mappedBy = "reservationStatus")
    @JsonIgnore
    private List<Reservation> reservations;
}