package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.sql.Connection;

public interface IUnitOfWork {
    Connection getConnection();

    void commit();

    void rollback();
}
