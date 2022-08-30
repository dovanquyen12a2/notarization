package com.vinorsoft.microservices.core.notarization.util.logging;

import lombok.Getter;

@Getter
public abstract class IRequestCorrelationIdentifier {
    protected String correlationID;
}
