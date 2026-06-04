package com.nexusflow.controller;

import com.nexusflow.entity.Notification;
import com.nexusflow.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for notification operations.
 * 
 * Handles notification retrieval, marking as read, and management.
 * 
 * @author NexusFlow Team
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Gets all notifications for a user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Notifications", 
               description = "Retrieve all notifications for a specific user")
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(
            @PathVariable Long userId) {
        List<Notification> notifications = notificationService.findByUserId(userId);
        return ResponseEntity.ok(notifications.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Gets unread notifications for a user.
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get Unread Notifications", 
               description = "Retrieve unread notifications for a specific user")
    public ResponseEntity<List<Map<String, Object>>> getUnreadNotifications(
            @PathVariable Long userId) {
        List<Notification> notifications = notificationService.findUnreadByUserId(userId);
        return ResponseEntity.ok(notifications.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Gets recent notifications for a user.
     */
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get Recent Notifications", 
               description = "Retrieve recent notifications for a user (limited count)")
    public ResponseEntity<List<Map<String, Object>>> getRecentNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Notification> notifications = notificationService.findRecentByUserId(userId, limit);
        return ResponseEntity.ok(notifications.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList()));
    }

    /**
     * Marks a notification as read.
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark Notification as Read", 
               description = "Mark a specific notification as read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Marks all notifications as read for a user.
     */
    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark All Notifications as Read", 
               description = "Mark all notifications as read for a specific user")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a notification.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Notification", 
               description = "Delete a specific notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets unread notification count for a user.
     */
    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Get Unread Count", 
               description = "Get count of unread notifications for a user")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", notificationService.countUnread(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new notification.
     */
    @PostMapping
    @Operation(summary = "Create Notification", 
               description = "Create a new notification for a user")
    public ResponseEntity<Map<String, Object>> createNotification(
            @RequestBody Map<String, Object> notificationData) {
        try {
            Notification notification = notificationService.createNotification(
                    getLong(notificationData, "userId"),
                    getString(notificationData, "title"),
                    getString(notificationData, "message"),
                    Notification.NotificationType.valueOf(
                            getString(notificationData, "type").toUpperCase())
            );
            
            return ResponseEntity.ok(convertToMap(notification));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to create notification: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper methods
    private Map<String, Object> convertToMap(Notification notification) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", notification.getId());
        map.put("userId", notification.getUser().getId());
        map.put("title", notification.getTitle());
        map.put("message", notification.getMessage());
        map.put("type", notification.getType().name());
        map.put("isRead", notification.getIsRead());
        map.put("relatedShipmentId", notification.getRelatedShipmentId());
        map.put("relatedSupplierId", notification.getRelatedSupplierId());
        map.put("createdAt", notification.getCreatedAt());
        return map;
    }

    private String getString(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }

    private Long getLong(Map<String, Object> map, String key) {
        if (map.get(key) == null) return null;
        if (map.get(key) instanceof Number) {
            return ((Number) map.get(key)).longValue();
        }
        return Long.parseLong(map.get(key).toString());
    }
}