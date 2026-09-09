package com.nexusflow.dto;

import java.time.LocalDateTime;

/**
 * DTO for Notification entity - used for API responses.
 * 
 * @author NexusFlow Team
 */
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Long relatedShipmentId;
    private Long relatedSupplierId;
    private LocalDateTime createdAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, Long userId, String title, String message, 
                           String type, Boolean isRead, Long relatedShipmentId, 
                           Long relatedSupplierId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedShipmentId = relatedShipmentId;
        this.relatedSupplierId = relatedSupplierId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
}