package com.urban.upark.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "Payment_status")
public class PaymentStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id_Payment_status;

    private String label;

    @Column(name = "value_")
    private Integer value;
    
    @OneToMany(mappedBy = "paymentStatus")
    @JsonIgnore
    private List<CommissionReceived> commissionsReceived;
}