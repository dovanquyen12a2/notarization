package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vinorsoft.microservices.core.notarization.util.repository_base.IDbConfiguration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Component("DbFactoryR2DBC")
public class DbFactory implements IDbFactory {

    private ConnectionFactory _connection;

    @Autowired
    private IDbConfiguration _dbConfiguration;

    @Override
    public ConnectionFactory Init() {
        if (_connection == null) {
            try {
                _connection = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                        .option(DRIVER, "oracle")
                        .option(HOST, _dbConfiguration.getOracleHost())
                        .option(PORT, _dbConfiguration.getOraclePort())
                        .option(DATABASE, _dbConfiguration.getOracleDatabase())
                        .option(USER, _dbConfiguration.getOracleUsername())
                        .option(PASSWORD, _dbConfiguration.getOraclePassword())
                        .build());
            } catch (Exception ex) {
                System.out.println("Could not connect to DB: " + ex.getMessage());
            }
        }
        return _connection;
    }

}
