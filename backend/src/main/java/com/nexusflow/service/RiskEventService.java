package com.nexusflow.service;

import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.RiskEvent.Severity;
import com.nexusflow.entity.RiskEvent.Status;
import java.util.List;

public interface RiskEventService {
    List<RiskEvent> getAllRiskEvents();
    RiskEvent getRiskEventById(Long id);
    List<RiskEvent> getRiskEventsByStatus(Status status);
    List<RiskEvent> getRiskEventsBySeverity(Severity severity);
    List<RiskEvent> getRecentRiskEvents(int count);
    RiskEvent saveRiskEvent(RiskEvent riskEvent);
    void deleteRiskEvent(Long id);
}
