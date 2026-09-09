package com.nexusflow.repository;

import com.nexusflow.entity.Notification;
import com.nexusflow.entity.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Repository interface for Notification entity operations.
 * 
 * @author NexusFlow Team
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds notifications for a specific user.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Finds unread notifications for a user.
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Finds notifications by type.
     */
    List<Notification> findByType(NotificationType type);

    /**
     * Counts unread notifications for a user.
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Counts notifications by type.
     */
    long countByType(NotificationType type);

    /**
     * Finds recent notifications for a user.
     */
    default List<Notification> findRecentNotifications(Long userId, int limit) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return findByUserId(userId, pageable).getContent();
    }

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds notifications related to a shipment.
     */
    List<Notification> findByRelatedShipmentIdOrderByCreatedAtDesc(Long shipmentId);

    /**
     * Finds notifications related to a supplier.
     */
    List<Notification> findByRelatedSupplierIdOrderByCreatedAtDesc(Long supplierId);

    /**
     * Gets notification count grouped by type.
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n GROUP BY n.type")
    List<Object[]> countByTypeGrouped();
}
