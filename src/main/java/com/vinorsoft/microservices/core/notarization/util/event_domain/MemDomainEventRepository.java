package com.vinorsoft.microservices.core.notarization.util.event_domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

@Component
public class MemDomainEventRepository implements IDomainEventRepository {
    private List<DomainEventRecord> domainEvents = new ArrayList<DomainEventRecord>();

    @Override
    public <TDomainEvent extends DomainEvent> void Add(TDomainEvent domainEvent) {
        DomainEventRecord record = new DomainEventRecord();
        record.setType(domainEvent.Type());
        record.setCreated(domainEvent.getCreated());
        record.setCorrelationID(domainEvent.getCorrelationID());

        List<KeyValuePair<String, String>> args = new ArrayList<KeyValuePair<String, String>>();
        for (Entry<String, Object> entry : domainEvent.getArgs().entrySet()) {
            args.add(new KeyValuePair<String, String>(entry.getKey(), entry.getValue().toString()));
        }
        record.setArgs(args);

        this.domainEvents.add(record);
    }

    @Override
    public Iterable<DomainEventRecord> FindAll() {
        return this.domainEvents;
    }

}
