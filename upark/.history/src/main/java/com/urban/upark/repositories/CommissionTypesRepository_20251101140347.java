package com.urban.upark.repositories;

import com.urban.upark.models.CommissionTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionTypesRepository extends JpaRepository<CommissionTypes, Integer> {
}