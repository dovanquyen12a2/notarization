package com.vinorsoft.microservices.core.notarization.util.event_domain;

public interface Handles<T extends DomainEvent> {

    void Handle(T args);
}
