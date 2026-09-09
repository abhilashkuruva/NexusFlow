package com.nexusflow.repository;

import com.nexusflow.entity.Shipment;
import com.nexusflow.entity.Shipment.ShipmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @EntityGraph(attributePaths = {"supplier", "product"})
    List<Shipment> findAll();

    @EntityGraph(attributePaths = {"supplier", "product"})
    Page<Shipment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"supplier", "product"})
    Shipment findByTrackingNumber(String trackingNumber);

    @EntityGraph(attributePaths = {"supplier", "product"})
    List<Shipment> findByStatus(ShipmentStatus status);

    @EntityGraph(attributePaths = {"supplier", "product"})
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"supplier", "product"})
    List<Shipment> findBySupplierId(Long supplierId);

    @EntityGraph(attributePaths = {"supplier", "product"})
    Page<Shipment> findBySupplierId(Long supplierId, Pageable pageable);

    List<Shipment> findByPriority(Shipment.Priority priority);

    @EntityGraph(attributePaths = {"supplier", "product"})
    @Query("SELECT s FROM Shipment s WHERE s.status = 'DELAYED' OR " +
           "(s.estimatedDeliveryDate < :today AND s.actualDeliveryDate IS NULL AND " +
           "(s.status = 'IN_TRANSIT' OR s.status = 'PENDING'))")
    List<Shipment> findDelayedShipments(@Param("today") LocalDate today);

    List<Shipment> findByOriginCountry(String originCountry);

    List<Shipment> findByDestinationCountry(String destinationCountry);

    @EntityGraph(attributePaths = {"supplier", "product"})
    @Query("SELECT s FROM Shipment s WHERE " +
           "LOWER(s.trackingNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.originCity) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.destinationCity) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Shipment> searchShipments(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM Shipment s WHERE s.shipmentDate BETWEEN :startDate AND :endDate")
    List<Shipment> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    long count();

    long countByStatus(ShipmentStatus status);

    long countBySupplierId(Long supplierId);

    @Query("SELECT s.status, COUNT(s) FROM Shipment s GROUP BY s.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT MONTH(s.shipmentDate), COUNT(s) FROM Shipment s " +
           "WHERE YEAR(s.shipmentDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(s.shipmentDate) ORDER BY MONTH(s.shipmentDate)")
    List<Object[]> getMonthlyShipmentCount();

    default List<Shipment> findTopByValue(int limit) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "valueUsd")
        );
        return findAll(pageable).getContent();
    }
}
