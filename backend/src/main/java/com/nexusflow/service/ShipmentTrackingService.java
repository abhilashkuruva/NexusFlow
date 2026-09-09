package com.nexusflow.service;

import com.nexusflow.entity.Shipment;
import com.nexusflow.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ShipmentTrackingService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final String[] checkpoints = {"Mumbai", "Dubai", "London", "New York", "Singapore"};

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void simulateShipmentMovement() {
        // Optimization: Only fetch shipments that are actually in transit
        List<Shipment> activeShipments = shipmentRepository.findByStatus(Shipment.ShipmentStatus.IN_TRANSIT);
        
        for (Shipment s : activeShipments) {
            // Cycle through checkpoints for simulation
            int nextIndex = (int) (Math.random() * checkpoints.length);
            s.setCurrentLocation(checkpoints[nextIndex]);
            // Note: repository.save(s) is not strictly needed here if @Transactional is used, 
            // as Hibernate's dirty checking will automatically flush changes.

            // Broadcast update
            Map<String, Object> update = new HashMap<>();
            update.put("trackingNumber", s.getTrackingNumber());
            update.put("location", s.getCurrentLocation());
            update.put("status", s.getStatus());
            
            messagingTemplate.convertAndSend("/topic/shipments", update);
        }
    }
}