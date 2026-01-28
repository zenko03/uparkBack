package com.urban.upark.repositories;

import com.urban.upark.models.OwnerDashboardSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerDashboardSummaryRepository extends JpaRepository<OwnerDashboardSummary, Integer> {
    Optional<OwnerDashboardSummary> findByOwnerId(Integer ownerId);
}
