package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;

import io.r2dbc.spi.ConnectionFactory;

public interface IUnitOfWork {
    ConnectionFactory getConnection();

    ReactiveTransactionManager getTransactionManager();

    void commit(ReactiveTransaction transaction);

    void rollback(ReactiveTransaction transaction);
}
