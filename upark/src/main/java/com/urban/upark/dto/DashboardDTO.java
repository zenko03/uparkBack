package com.urban.upark.dto;

import com.urban.upark.models.OwnerDashboardSummary;
import com.urban.upark.models.OwnerNotification;
import com.urban.upark.models.OwnerReservationsStats;
import com.urban.upark.models.OwnerWeeklyRevenue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private OwnerDashboardSummary summary;
    private OwnerReservationsStats reservationsStats;
    private List<OwnerWeeklyRevenue> weeklyRevenue;
    private List<OwnerNotification> recentNotifications;
}
