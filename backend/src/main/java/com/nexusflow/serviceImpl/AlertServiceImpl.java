package com.nexusflow.serviceImpl;

import com.nexusflow.entity.Alert;
import com.nexusflow.repository.AlertRepository;
import com.nexusflow.service.AlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    public Alert getAlertById(Long id) {
        return alertRepository.findById(id).orElse(null);
    }

    @Override
    public List<Alert> getAlertsByRiskEventId(Long riskEventId) {
        return alertRepository.findByRiskEventIdOrderByCreatedAtDesc(riskEventId);
    }

    @Override
    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverityOrderByCreatedAtDesc(severity);
    }

    @Override
    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}
