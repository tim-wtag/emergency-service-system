package com.emergency.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
//@Inheritance (strategy = InheritanceType.JOINED)
public class EmergencyDispatch extends BaseEntity {
    private String description;

    @Enumerated(EnumType.STRING)
    private EmergencyType type;
    private LocalTime dispatchedTime;
    private LocalTime resolvedTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private EmergencyIncident parentIncident;

    public EmergencyDispatch(){
        super();
    }

    public EmergencyDispatch(EmergencyType type, EmergencyIncident incident) {
        super();
        this.type = type;
        this.parentIncident = incident;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmergencyType getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public EmergencyIncident getParentIncident() {
        return parentIncident;
    }

    public void setParentIncident(EmergencyIncident parentIncident) {
        this.parentIncident = parentIncident;
    }

    public LocalTime getDispatchedTime() {
        return dispatchedTime;
    }

    public void setDispatchedTime(LocalTime time) {
        this.dispatchedTime = time;
    }

    public LocalTime getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(LocalTime time) {
        this.resolvedTime = time;
    }
}