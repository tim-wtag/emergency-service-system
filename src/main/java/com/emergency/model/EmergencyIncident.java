package com.emergency.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.InheritanceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;

@Entity
//@Inheritance (strategy = InheritanceType.JOINED)
public class EmergencyIncident extends BaseEntity {
    private String description;
    private LocalTime reportedTime;
    
    private LocalTime resolvedTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToMany(mappedBy = "parentIncident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmergencyDispatch> dispatches = new ArrayList<>();

    public EmergencyIncident() {
        super();
    }

    public EmergencyIncident(String description) {
        super();
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalTime getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(LocalTime time) {
        this.reportedTime = time;
    }

    public LocalTime getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(LocalTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }
}