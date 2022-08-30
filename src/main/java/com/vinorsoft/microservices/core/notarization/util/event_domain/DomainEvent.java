package com.vinorsoft.microservices.core.notarization.util.event_domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class DomainEvent implements Serializable {

    public DomainEvent() {
        this.created = new Date();
        this.args = new HashMap<String, Object>();
    }

    public String Type() {
        return this.getClass().getName();
    }

    private Date created;

    public Date getCreated() {
        return created;
    }

    private Map<String, Object> args;

    public Map<String, Object> getArgs() {
        return args;
    }

    private String correlationID;

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public abstract void Flatten();
}
