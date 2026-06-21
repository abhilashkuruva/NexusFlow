package com.nexusflow.service;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Supplier;
import com.nexusflow.repository.ShipmentRepository;
import com.nexusflow.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Shipment-related business logic.
 * 
 * Handles shipment CRUD operations, tracking, and status updates.
 * 
 * @author NexusFlow Team
 */
@Service
@Transactional
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Finds a shipment by ID.
     */
    public Optional<Shipment> findById(Long id) {
        return shipmentRepository.findById(id);
    }

    /**
     * Finds a shipment by tracking number.
     */
    public Shipment findByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    /**
     * Finds all shipments.
     */
    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    /**
     * Finds shipments by status.
     */
    public List<Shipment> findByStatus(Shipment.ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    /**
     * Finds shipments by supplier.
     */
    public List<Shipment> findBySupplierId(Long supplierId) {
        return shipmentRepository.findBySupplierId(supplierId);
    }

    /**
     * Searches shipments by tracking number or route.
     */
    public List<Shipment> searchShipments(String searchTerm) {
        return shipmentRepository.searchShipments(searchTerm);
    }

    /**
     * Finds delayed shipments.
     */
    public List<Shipment> findDelayedShipments() {
        return shipmentRepository.findDelayedShipments(LocalDate.now());
    }

    /**
     * Generates a unique tracking number.
     */
    private String generateTrackingNumber() {
        return "NFS-" + LocalDate.now().getYear() + "-" + 
               String.format("%03d", (int)(Math.random() * 1000));
    }

    /**
     * Creates a new shipment.
     */
    public Shipment createShipment(Long supplierId, String originCity, String originCountry,
                                   String destinationCity, String destinationCountry,
                                   LocalDate shipmentDate, LocalDate estimatedDeliveryDate,
                                   String cargoType, java.math.BigDecimal weightKg,
                                   java.math.BigDecimal valueUsd, String priority, String notes) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        
        if (supplierOpt.isEmpty()) {
            throw new RuntimeException("Supplier not found");
        }

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setSupplier(supplierOpt.get());
        shipment.setOriginCity(originCity);
        shipment.setOriginCountry(originCountry);
        shipment.setDestinationCity(destinationCity);
        shipment.setDestinationCountry(destinationCountry);
        shipment.setShipmentDate(shipmentDate);
        shipment.setEstimatedDeliveryDate(estimatedDeliveryDate);
        shipment.setCargoType(cargoType);
        shipment.setWeightKg(weightKg);
        shipment.setValueUsd(valueUsd);
        
        if (priority != null) {
            shipment.setPriority(Shipment.Priority.valueOf(priority.toUpperCase()));
        }
        
        shipment.setNotes(notes);

        return shipmentRepository.save(shipment);
    }

    /**
     * Updates an existing shipment.
     */
    public Shipment updateShipment(Long id, String originCity, String originCountry,
                                   String destinationCity, String destinationCountry,
                                   LocalDate shipmentDate, LocalDate estimatedDeliveryDate,
                                   String cargoType, java.math.BigDecimal weightKg,
                                   java.math.BigDecimal valueUsd, String priority, String notes) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(id);
        
        if (shipmentOpt.isEmpty()) {
            throw new RuntimeException("Shipment not found");
        }

        Shipment shipment = shipmentOpt.get();
        
        if (originCity != null) shipment.setOriginCity(originCity);
        if (originCountry != null) shipment.setOriginCountry(originCountry);
        if (destinationCity != null) shipment.setDestinationCity(destinationCity);
        if (destinationCountry != null) shipment.setDestinationCountry(destinationCountry);
        if (shipmentDate != null) shipment.setShipmentDate(shipmentDate);
        if (estimatedDeliveryDate != null) shipment.setEstimatedDeliveryDate(estimatedDeliveryDate);
        if (cargoType != null) shipment.setCargoType(cargoType);
        if (weightKg != null) shipment.setWeightKg(weightKg);
        if (valueUsd != null) shipment.setValueUsd(valueUsd);
        if (priority != null) shipment.setPriority(Shipment.Priority.valueOf(priority.toUpperCase()));
        if (notes != null) shipment.setNotes(notes);

        return shipmentRepository.save(shipment);
    }

    /**
     * Updates shipment status.
     */
    public Shipment updateStatus(Long id, Shipment.ShipmentStatus status) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(id);
        
        if (shipmentOpt.isEmpty()) {
            throw new RuntimeException("Shipment not found");
        }

        Shipment shipment = shipmentOpt.get();
        shipment.setStatus(status);
        
        if (status == Shipment.ShipmentStatus.DELIVERED) {
            shipment.setActualDeliveryDate(LocalDate.now());
        }

        return shipmentRepository.save(shipment);
    }

    /**
     * Cancels a shipment.
     */
    public void cancelShipment(Long id) {
        updateStatus(id, Shipment.ShipmentStatus.CANCELLED);
    }

    /**
     * Marks a shipment as delivered.
     */
    public void markAsDelivered(Long id) {
        updateStatus(id, Shipment.ShipmentStatus.DELIVERED);
    }

    /**
     * Deletes a shipment.
     */
    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    /**
     * Gets shipment statistics.
     */
    public long countShipments() {
        return shipmentRepository.count();
    }

    /**
     * Gets shipments by date range.
     */
    public List<Shipment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return shipmentRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Gets top shipments by value.
     */
    public List<Shipment> findTopByValue(int limit) {
        return shipmentRepository.findTopByValue(limit);
    }

    /**
     * Gets shipment count by status.
     */
    public List<Object[]> getCountByStatus() {
        return shipmentRepository.countByStatusGrouped();
    }

    /**
     * Gets monthly shipment count.
     */
    public List<Object[]> getMonthlyShipmentCount() {
        return shipmentRepository.getMonthlyShipmentCount();
    }
}