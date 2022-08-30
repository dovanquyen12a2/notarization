package com.vinorsoft.microservices.core.notarization.util.event_domain;

import org.springframework.stereotype.Component;

import com.vinorsoft.microservices.core.notarization.util.logging.IRequestCorrelationIdentifier;

@Component
public class DomainEventHandle<TDomainEvent extends DomainEvent> implements Handles<TDomainEvent> {
    private IDomainEventRepository domainEventRepository;
    private IRequestCorrelationIdentifier requestCorrelationIdentifier;

    public DomainEventHandle(IDomainEventRepository domainEventRepository,
            IRequestCorrelationIdentifier requestCorrelationIdentifier) {
        this.domainEventRepository = domainEventRepository;
        this.requestCorrelationIdentifier = requestCorrelationIdentifier;
    }

    public void Handle(TDomainEvent event) {
        event.Flatten();
        event.setCorrelationID(this.requestCorrelationIdentifier.getCorrelationID());
        this.domainEventRepository.Add(event);
    }
}
