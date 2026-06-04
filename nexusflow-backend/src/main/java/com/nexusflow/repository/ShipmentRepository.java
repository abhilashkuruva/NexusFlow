package com.nexusflow.repository;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Shipment.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Shipment entity operations.
 * 
 * Provides CRUD operations and custom query methods for
 * managing shipment data in the database.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /**
     * Finds a shipment by its tracking number.
     */
    Shipment findByTrackingNumber(String trackingNumber);

    /**
     * Finds shipments by their current status.
     */
    List<Shipment> findByStatus(ShipmentStatus status);

    /**
     * Finds shipments by supplier ID.
     */
    List<Shipment> findBySupplierId(Long supplierId);

    /**
     * Finds shipments with a specific priority.
     */
    List<Shipment> findByPriority(Shipment.Priority priority);

    /**
     * Finds delayed shipments.
     */
    @Query("SELECT s FROM Shipment s WHERE s.status = 'DELAYED' OR " +
           "(s.estimatedDeliveryDate < :today AND s.actualDeliveryDate IS NULL AND " +
           "(s.status = 'IN_TRANSIT' OR s.status = 'PENDING'))")
    List<Shipment> findDelayedShipments(@Param("today") LocalDate today);

    /**
     * Finds shipments by origin country.
     */
    List<Shipment> findByOriginCountry(String originCountry);

    /**
     * Finds shipments by destination country.
     */
    List<Shipment> findByDestinationCountry(String destinationCountry);

    /**
     * Searches shipments by tracking number or route.
     */
    @Query("SELECT s FROM Shipment s WHERE " +
           "LOWER(s.trackingNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.originCity) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.destinationCity) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Shipment> searchShipments(@Param("searchTerm") String searchTerm);

    /**
     * Finds shipments within a date range.
     */
    @Query("SELECT s FROM Shipment s WHERE s.shipmentDate BETWEEN :startDate AND :endDate")
    List<Shipment> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    /**
     * Counts total shipments.
     */
    long count();

    /**
     * Counts shipments by status.
     */
    long countByStatus(ShipmentStatus status);

    /**
     * Counts shipments by supplier.
     */
    long countBySupplierId(Long supplierId);

    /**
     * Gets shipment statistics grouped by status.
     */
    @Query("SELECT s.status, COUNT(s) FROM Shipment s GROUP BY s.status")
    List<Object[]> countByStatusGrouped();

    /**
     * Gets monthly shipment count for the current year.
     */
    @Query("SELECT MONTH(s.shipmentDate), COUNT(s) FROM Shipment s " +
           "WHERE YEAR(s.shipmentDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(s.shipmentDate) ORDER BY MONTH(s.shipmentDate)")
    List<Object[]> getMonthlyShipmentCount();

    /**
     * Finds top shipments by value.
     */
    @Query("SELECT s FROM Shipment s ORDER BY s.valueUsd DESC LIMIT :limit")
    List<Shipment> findTopByValue(@Param("limit") int limit);
}