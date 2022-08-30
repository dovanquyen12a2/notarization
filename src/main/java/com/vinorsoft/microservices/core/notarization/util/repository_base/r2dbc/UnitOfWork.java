package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;

import io.r2dbc.spi.ConnectionFactory;

@Component("UnitOfWorkR2DBC")
public class UnitOfWork implements IUnitOfWork {
    private final IDbFactory _dbFactory;

    @Autowired
    public UnitOfWork(IDbFactory dbFactory) {
        this._dbFactory = dbFactory;
        this._id = UUID.randomUUID();
    }

    private ConnectionFactory _connection;
    private ReactiveTransactionManager _transactionManager;

    @Override
    public ConnectionFactory getConnection() {
        if (_connection == null) {
            _connection = _dbFactory.Init();
        }
        return _connection;
    }

    private UUID _id = null;

    public UUID getId() {
        return _id;
    }

    public void commit(ReactiveTransaction transaction) {
        _transactionManager.commit(transaction);
        dispose();
    }

    public void rollback(ReactiveTransaction transaction) {
        _transactionManager.rollback(transaction);
        dispose();
    }

    public void dispose() {
    }

    @Override
    public ReactiveTransactionManager getTransactionManager() {
        if (_transactionManager == null) {
            _transactionManager = new R2dbcTransactionManager(getConnection());
        }
        return _transactionManager;
    }
}
