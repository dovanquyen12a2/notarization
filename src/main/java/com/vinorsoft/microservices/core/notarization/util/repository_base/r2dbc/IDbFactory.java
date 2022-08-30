package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import io.r2dbc.spi.ConnectionFactory;

public interface IDbFactory {
    ConnectionFactory Init();
}
