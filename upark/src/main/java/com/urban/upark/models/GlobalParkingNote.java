package com.urban.upark.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "v_global_parking_note")
public class GlobalParkingNote {

    @Id
    @Column(name = "id_parking")
    private int idParking;

    @Column(name = "label")
    private String label;

    @Column(name = "average")
    private Double average;

    @Column(name = "id_users")
    private int idUsers;
}
