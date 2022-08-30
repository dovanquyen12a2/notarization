package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vinorsoft.microservices.core.notarization.util.repository_base.IDbConfiguration;

@Component("DbFactoryJDBC")
public class DbFactory implements IDbFactory {

    private Connection _connection;

    @Autowired
    private IDbConfiguration _dbConfiguration;

    @Override
    public Connection Init() {
        if (_connection == null) {
            try {
                Class.forName(_dbConfiguration.getOracleDriverClassName());
                _connection = DriverManager.getConnection(_dbConfiguration.getOracleConnectionString(),
                        _dbConfiguration.getOracleUsername(), _dbConfiguration.getOraclePassword());
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load JDBC driver: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Could not connect to DB: " + e.getMessage());
            }
        }
        return _connection;
    }
}
