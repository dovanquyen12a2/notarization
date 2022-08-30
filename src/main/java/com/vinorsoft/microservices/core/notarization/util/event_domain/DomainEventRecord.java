package com.vinorsoft.microservices.core.notarization.util.event_domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DomainEventRecord implements Serializable {
    private String type;
    private List<KeyValuePair<String, String>> args;
    private String correlationID;
    private Date created;
}
