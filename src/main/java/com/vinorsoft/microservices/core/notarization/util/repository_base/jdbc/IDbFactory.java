package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.sql.Connection;

public interface IDbFactory {
    Connection Init();
}
