package com.emergency.model;

public class EmergencyDispatch extends BaseEntity {
    private String comment;
    private EmergencyType type;
    private Status status = Status.PENDING;
    
    private EmergencyIncident parentIncident; 

    public EmergencyDispatch(EmergencyType type, EmergencyIncident incident) {
        super();
        this.type = type;
        this.parentIncident = incident;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String description){
        this.comment = description;
    }

    public EmergencyType getType() {
        return type;
    }

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public EmergencyIncident getParentIncident() {
        return parentIncident;
    }

    public void setParentIncident(EmergencyIncident parentIncident) {
        this.parentIncident = parentIncident;
    }
}