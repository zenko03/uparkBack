package com.urban.upark.repositories;

import com.urban.upark.models.GlobalCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalCommissionRepository extends JpaRepository<GlobalCommission, Integer> {
}