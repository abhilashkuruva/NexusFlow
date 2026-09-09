package com.nexusflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "related_shipment_id")
    private Long relatedShipmentId;

    @Column(name = "related_supplier_id")
    private Long relatedSupplierId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum NotificationType {
        INFO, WARNING, ALERT, SUCCESS
    }

    public Notification() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Long getRelatedShipmentId() { return relatedShipmentId; }
    public void setRelatedShipmentId(Long relatedShipmentId) { this.relatedShipmentId = relatedShipmentId; }

    public Long getRelatedSupplierId() { return relatedSupplierId; }
    public void setRelatedSupplierId(Long relatedSupplierId) { this.relatedSupplierId = relatedSupplierId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}