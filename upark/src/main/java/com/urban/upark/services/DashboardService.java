package com.urban.upark.services;

import com.urban.upark.dto.DashboardDTO;
import com.urban.upark.models.OwnerDashboardSummary;
import com.urban.upark.models.OwnerNotification;
import com.urban.upark.models.OwnerReservationsStats;
import com.urban.upark.models.OwnerWeeklyRevenue;
import com.urban.upark.repositories.OwnerDashboardSummaryRepository;
import com.urban.upark.repositories.OwnerNotificationRepository;
import com.urban.upark.repositories.OwnerReservationsStatsRepository;
import com.urban.upark.repositories.OwnerWeeklyRevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OwnerDashboardSummaryRepository summaryRepository;
    private final OwnerReservationsStatsRepository reservationsStatsRepository;
    private final OwnerNotificationRepository notificationRepository;
    private final OwnerWeeklyRevenueRepository weeklyRevenueRepository;

    public DashboardDTO getOwnerDashboard(Integer ownerId) {
        OwnerDashboardSummary summary = summaryRepository.findByOwnerId(ownerId).orElse(null);
        OwnerReservationsStats reservationsStats = reservationsStatsRepository.findByOwnerId(ownerId).orElse(null);
        List<OwnerNotification> notifications = notificationRepository.findByOwnerIdOrderBySentAtDesc(ownerId);
        List<OwnerWeeklyRevenue> weeklyRevenue = weeklyRevenueRepository.findByOwnerIdOrderByJourAsc(ownerId);

        return DashboardDTO.builder()
                .summary(summary)
                .reservationsStats(reservationsStats)
                .recentNotifications(notifications)
                .weeklyRevenue(weeklyRevenue)
                .build();
    }
}