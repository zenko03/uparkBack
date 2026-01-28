package com.urban.upark.repositories;

import com.urban.upark.models.OwnerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerNotificationRepository extends JpaRepository<OwnerNotification, Integer> {
    List<OwnerNotification> findByOwnerIdOrderBySentAtDesc(Integer ownerId);

    List<OwnerNotification> findByOwnerIdAndReadFalse(Integer ownerId);
}
