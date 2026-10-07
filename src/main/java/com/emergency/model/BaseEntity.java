package com.emergency.model;

import java.util.UUID;

//This base class will be reused by all model objects and core fields such as id and its logic will be handle onlu
//here.
public abstract class BaseEntity {
    public UUID id;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id){
        this.id = id;
    }

}
