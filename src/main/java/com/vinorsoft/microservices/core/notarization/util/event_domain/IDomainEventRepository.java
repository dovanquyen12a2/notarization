package com.vinorsoft.microservices.core.notarization.util.event_domain;

public interface IDomainEventRepository {
    <TDomainEvent extends DomainEvent> void Add(TDomainEvent domainEvent);

    Iterable<DomainEventRecord> FindAll();
}
