package com.nexusflow.service;

import com.nexusflow.entity.Notification;
import com.nexusflow.entity.User;
import com.nexusflow.repository.NotificationRepository;
import com.nexusflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Notification management.
 * 
 * Handles creation, retrieval, and management of user notifications.
 * 
 * @author NexusFlow Team
 */
@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds a notification by ID.
     */
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    /**
     * Gets all notifications for a user.
     */
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Gets unread notifications for a user.
     */
    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Gets recent notifications for a user.
     */
    public List<Notification> findRecentByUserId(Long userId, int limit) {
        return notificationRepository.findRecentNotifications(userId, limit);
    }

    /**
     * Creates a new notification.
     */
    public Notification createNotification(Long userId, String title, String message, 
                                           Notification.NotificationType type) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Notification notification = new Notification();
        notification.setUser(userOpt.get());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * Creates a notification related to a shipment.
     */
    public Notification createShipmentNotification(Long userId, String title, String message, 
                                                   Notification.NotificationType type, 
                                                   Long shipmentId) {
        Notification notification = createNotification(userId, title, message, type);
        notification.setRelatedShipmentId(shipmentId);
        return notificationRepository.save(notification);
    }

    /**
     * Creates a notification related to a supplier.
     */
    public Notification createSupplierNotification(Long userId, String title, String message, 
                                                   Notification.NotificationType type, 
                                                   Long supplierId) {
        Notification notification = createNotification(userId, title, message, type);
        notification.setRelatedSupplierId(supplierId);
        return notificationRepository.save(notification);
    }

    /**
     * Marks a notification as read.
     */
    public void markAsRead(Long id) {
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Marks all notifications as read for a user.
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unread = findUnreadByUserId(userId);
        
        for (Notification notification : unread) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Deletes a notification.
     */
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    /**
     * Gets unread notification count for a user.
     */
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Sends alert notifications to all users with specific roles.
     */
    public void sendAlertToUsers(String title, String message, 
                                 Notification.NotificationType type, 
                                 User.Role[] roles) {
        for (User.Role role : roles) {
            List<User> users = userRepository.findByRole(role);
            
            for (User user : users) {
                createNotification(user.getId(), title, message, type);
            }
        }
    }

    /**
     * Gets notifications by type.
     */
    public List<Notification> findByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }

    /**
     * Gets notification statistics.
     */
    public List<Object[]> getCountByType() {
        return notificationRepository.countByTypeGrouped();
    }
}