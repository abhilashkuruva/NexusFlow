package com.nexusflow.repository;

import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.RiskEvent.Severity;
import com.nexusflow.entity.RiskEvent.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {

    List<RiskEvent> findByStatus(Status status);

    List<RiskEvent> findBySeverity(Severity severity);

    List<RiskEvent> findTop10ByStatusOrderByCreatedAtDesc(Status status);
}

