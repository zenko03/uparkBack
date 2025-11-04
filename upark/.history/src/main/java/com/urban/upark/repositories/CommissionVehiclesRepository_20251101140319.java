package com.urban.upark.repositories;

import com.urban.upark.models.CommissionVehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionVehiclesRepository extends JpaRepository<CommissionVehicles, Integer> {
}