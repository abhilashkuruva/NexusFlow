package com.nexusflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a notification for a user.
 * 
 * Notifications are used to alert users about important events
 * such as high-risk shipments, delays, delivery confirmations,
 * and system updates.
 * 
 * @author NexusFlow Team
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "related_shipment_id")
    private Long relatedShipmentId;

    @Column(name = "related_supplier_id")
    private Long relatedSupplierId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Enum representing notification types.
     */
    public enum NotificationType {
        INFO,      // General information
        WARNING,   // Warning about potential issues
        ALERT,     // Critical alert requiring attention
        SUCCESS    // Success confirmation
    }

    // Constructors
    public Notification() {
        this.isRead = false;
        this.type = NotificationType.INFO;
    }

    public Notification(User user, String title, String message, NotificationType type) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Long getRelatedShipmentId() {
        return relatedShipmentId;
    }

    public void setRelatedShipmentId(Long relatedShipmentId) {
        this.relatedShipmentId = relatedShipmentId;
    }

    public Long getRelatedSupplierId() {
        return relatedSupplierId;
    }

    public void setRelatedSupplierId(Long relatedSupplierId) {
        this.relatedSupplierId = relatedSupplierId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", isRead=" + isRead +
                '}';
    }
}