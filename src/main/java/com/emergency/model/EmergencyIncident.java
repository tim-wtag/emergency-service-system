package com.emergency.model;

/*
This Java object is a mapping for T_INCIDENT
*/
public class EmergencyIncident extends BaseEntity {
    private String description;
    private Status status = Status.PENDING;

    public EmergencyIncident(String description) {
        super();
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

}
