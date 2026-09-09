package com.nexusflow.service;

import com.nexusflow.entity.Alert;
import java.util.List;

public interface AlertService {
    List<Alert> getAllAlerts();
    Alert getAlertById(Long id);
    List<Alert> getAlertsByRiskEventId(Long riskEventId);
    List<Alert> getAlertsBySeverity(String severity);
    Alert saveAlert(Alert alert);
    void deleteAlert(Long id);
}
