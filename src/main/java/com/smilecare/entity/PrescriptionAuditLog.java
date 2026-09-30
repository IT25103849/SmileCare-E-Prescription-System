package com.smilecare.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_audit_logs")
public class PrescriptionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "prescription_id",
            nullable = false
    )
    private Long prescriptionId;

    @Column(nullable = false, length = 40)
    private String action;

    @Column(
            name = "actor_username",
            nullable = false,
            length = 100
    )
    private String actorUsername;

    @Column(
            name = "action_at",
            nullable = false
    )
    private LocalDateTime actionAt;

    @Column(length = 1000)
    private String details;

    public PrescriptionAuditLog() {
    }

    @PrePersist
    public void beforeInsert() {

        if (actionAt == null) {
            actionAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public LocalDateTime getActionAt() {
        return actionAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}