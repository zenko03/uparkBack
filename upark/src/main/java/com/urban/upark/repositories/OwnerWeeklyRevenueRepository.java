package com.urban.upark.repositories;

import com.urban.upark.models.OwnerWeeklyRevenue;
import com.urban.upark.models.OwnerWeeklyRevenueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerWeeklyRevenueRepository extends JpaRepository<OwnerWeeklyRevenue, OwnerWeeklyRevenueId> {
    List<OwnerWeeklyRevenue> findByOwnerIdOrderByJourAsc(Integer ownerId);
}
