package com.nexusflow.repository;

import com.nexusflow.entity.ShipmentRoute;
import com.nexusflow.entity.ShipmentRoute.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ShipmentRoute entity operations.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface ShipmentRouteRepository extends JpaRepository<ShipmentRoute, Long> {

    /**
     * Finds all route checkpoints for a shipment.
     */
    List<ShipmentRoute> findByShipmentIdOrderByCreatedAtAsc(Long shipmentId);

    /**
     * Finds route checkpoints by their status.
     */
    List<ShipmentRoute> findByStatus(RouteStatus status);

    /**
     * Finds checkpoints for a shipment that have been reached.
     */
    @Query("SELECT sr FROM ShipmentRoute sr WHERE sr.shipment.id = :shipmentId " +
           "AND sr.status IN ('ARRIVED', 'DEPARTED')")
    List<ShipmentRoute> findCompletedCheckpoints(@Param("shipmentId") Long shipmentId);

    /**
     * Finds pending checkpoints for a shipment.
     */
    @Query("SELECT sr FROM ShipmentRoute sr WHERE sr.shipment.id = :shipmentId " +
           "AND sr.status = 'PENDING'")
    List<ShipmentRoute> findPendingCheckpoints(@Param("shipmentId") Long shipmentId);
}