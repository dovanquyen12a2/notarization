package com.vinorsoft.microservices.core.notarization.util.event_domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class DomainEvents {

    @Autowired
    private static List<Handles<DomainEvent>> listHandles;

    public static <T extends DomainEvent> void Raise(T args) {
        for (Handles<DomainEvent> handles : listHandles) {
            handles.Handle(args);
        }
    }
}