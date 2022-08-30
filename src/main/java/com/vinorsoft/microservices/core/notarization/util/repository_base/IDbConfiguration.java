package com.vinorsoft.microservices.core.notarization.util.repository_base;

public interface IDbConfiguration {
    String getOracleDriverClassName();

    String getOracleConnectionString();

    String getOracleDatabase();

    String getOracleUsername();

    String getOraclePassword();

    String getOracleHost();

    Integer getOraclePort();
}
