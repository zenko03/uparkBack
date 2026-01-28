package com.urban.upark.models;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerWeeklyRevenueId implements Serializable {
    private Integer ownerId;
    private LocalDate jour;
}
