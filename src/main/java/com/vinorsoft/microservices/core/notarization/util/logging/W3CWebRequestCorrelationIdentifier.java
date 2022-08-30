package com.vinorsoft.microservices.core.notarization.util.logging;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class W3CWebRequestCorrelationIdentifier extends IRequestCorrelationIdentifier {
    private String correlationID;

    public W3CWebRequestCorrelationIdentifier() {
        this.correlationID = UUID.randomUUID().toString();
    }

    @Override
    public String getCorrelationID() {
        return correlationID;
    }
}
