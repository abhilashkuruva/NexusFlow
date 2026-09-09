package com.nexusflow.serviceImpl;

import com.nexusflow.entity.RiskEvent;
import com.nexusflow.entity.RiskEvent.Status;
import com.nexusflow.entity.RiskEvent.Severity;
import com.nexusflow.repository.RiskEventRepository;
import com.nexusflow.service.RiskEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RiskEventServiceImpl implements RiskEventService {

    private final RiskEventRepository riskEventRepository;

    public RiskEventServiceImpl(RiskEventRepository riskEventRepository) {
        this.riskEventRepository = riskEventRepository;
    }

    @Override
    public List<RiskEvent> getAllRiskEvents() {
        return riskEventRepository.findAll();
    }

    @Override
    public RiskEvent getRiskEventById(Long id) {
        Optional<RiskEvent> opt = riskEventRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public List<RiskEvent> getRiskEventsByStatus(Status status) {
        return riskEventRepository.findByStatus(status);
    }

    @Override
    public List<RiskEvent> getRiskEventsBySeverity(Severity severity) {
        return riskEventRepository.findBySeverity(severity);
    }

    @Override
    public List<RiskEvent> getRecentRiskEvents(int count) {
        return riskEventRepository.findTop10ByStatusOrderByCreatedAtDesc(Status.NEW);
    }

    @Override
    public RiskEvent saveRiskEvent(RiskEvent riskEvent) {
        return riskEventRepository.save(riskEvent);
    }

    @Override
    public void deleteRiskEvent(Long id) {
        riskEventRepository.deleteById(id);
    }
}
