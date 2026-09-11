package com.emergency.model;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EmergencyIncident {
    private static final AtomicInteger incidentId = new AtomicInteger(0);
    private String description;
    private IncidentType type;
    private IncidentStatus status = IncidentStatus.PENDING;
    private int id;

    public EmergencyIncident(String description, IncidentType type) {
        this.description = description;
        this.type = type;
        this.id = incidentId.getAndIncrement();
    }

    public String getDescription() {
        return description;
    }

    public IncidentType getType() {
        return type;
    }

    public IncidentStatus getStatus(){
        return status;
    }

    public void setStatus(IncidentStatus status){
        this.status = status;
    }

    public int getId(){
        return id;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
