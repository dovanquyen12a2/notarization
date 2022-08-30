package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component("UnitOfWorkJDBC")
public class UnitOfWork implements IUnitOfWork {
    private final IDbFactory _dbFactory;

    @Autowired
    public UnitOfWork(IDbFactory dbFactory) {
        this._dbFactory = dbFactory;
        this._id = UUID.randomUUID();
    }

    private Connection _connection;

    public Connection getConnection() {
        if (_connection == null)
            _connection = _dbFactory.Init();
        return _connection;
    }

    private UUID _id = null;

    public UUID getId() {
        return _id;
    }

    public void commit() {
        try {
            _connection.commit();
            _connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e);
        }

        dispose();
    }

    public void rollback() {
        try {
            _connection.rollback();
            _connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e);
        }
        dispose();
    }

    public void dispose() {
    }
}
