package com.urban.upark.repositories;

import com.urban.upark.models.DisputeProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeProofRepository extends JpaRepository<DisputeProof, Long> {
    List<DisputeProof> findByDispute_Id(Long disputeId);
}
