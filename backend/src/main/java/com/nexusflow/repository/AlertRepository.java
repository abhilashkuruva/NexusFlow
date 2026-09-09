package com.nexusflow.repository;

import com.nexusflow.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByRiskEventIdOrderByCreatedAtDesc(Long riskEventId);

    List<Alert> findBySeverityOrderByCreatedAtDesc(String severity);

    long countBySeverity(String severity);
}
