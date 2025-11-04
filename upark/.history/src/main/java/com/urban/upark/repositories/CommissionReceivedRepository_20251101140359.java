package com.urban.upark.repositories;

import com.urban.upark.models.CommissionReceived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionReceivedRepository extends JpaRepository<CommissionReceived, Integer> {
}