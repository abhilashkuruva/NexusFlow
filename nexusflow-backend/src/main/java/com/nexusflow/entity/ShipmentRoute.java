package com.nexusflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a checkpoint/route point in a shipment's journey.
 * 
 * Each shipment can have multiple route checkpoints that track its
 * progress from origin to destination. This allows for detailed
 * tracking of shipment movement through various transit points.
 * 
 * @author NexusFlow Team
 */
@Entity
@Table(name = "shipment_routes")
public class ShipmentRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "checkpoint_name", nullable = false)
    private String checkpointName;

    @Column(name = "checkpoint_city")
    private String checkpointCity;

    @Column(name = "checkpoint_country")
    private String checkpointCountry;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Enumerated(EnumType.STRING)
    private RouteStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Enum representing the status of a route checkpoint.
     */
    public enum RouteStatus {
        PENDING,    // Not yet reached
        ARRIVED,    // Arrived at checkpoint
        DEPARTED,   // Left the checkpoint
        SKIPPED     // Checkpoint was skipped
    }

    // Constructors
    public ShipmentRoute() {
        this.status = RouteStatus.PENDING;
    }

    public ShipmentRoute(Shipment shipment, String checkpointName, String checkpointCity, 
                         String checkpointCountry) {
        this();
        this.shipment = shipment;
        this.checkpointName = checkpointName;
        this.checkpointCity = checkpointCity;
        this.checkpointCountry = checkpointCountry;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getCheckpointName() {
        return checkpointName;
    }

    public void setCheckpointName(String checkpointName) {
        this.checkpointName = checkpointName;
    }

    public String getCheckpointCity() {
        return checkpointCity;
    }

    public void setCheckpointCity(String checkpointCity) {
        this.checkpointCity = checkpointCity;
    }

    public String getCheckpointCountry() {
        return checkpointCountry;
    }

    public void setCheckpointCountry(String checkpointCountry) {
        this.checkpointCountry = checkpointCountry;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public RouteStatus getStatus() {
        return status;
    }

    public void setStatus(RouteStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ShipmentRoute{" +
                "id=" + id +
                ", checkpointName='" + checkpointName + '\'' +
                ", status=" + status +
                '}';
    }
}