package com.vinorsoft.microservices.core.notarization.util.repository_base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DbConfiguration implements IDbConfiguration {
    @Autowired
    private Environment env;

    public String getOracleDriverClassName() {
        return env.getProperty("spring.datasource.driver-class-name");
    }

    public String getOracleConnectionString() {
        return env.getProperty("spring.datasource.url");
    }

    public String getOracleUsername() {
        return env.getProperty("spring.datasource.username");
    }

    public String getOraclePassword() {
        return env.getProperty("spring.datasource.password");
    }

    @Override
    public String toString() {
        return "Oracle config: url" + env.getProperty("spring.datasource.url") + "\nUsername: "
                + env.getProperty("spring.datasource.username");
    }

    @Override
    public String getOracleDatabase() {
        return env.getProperty("spring.datasource.database");
    }

    @Override
    public String getOracleHost() {
        return env.getProperty("spring.datasource.host");
    }

    @Override
    public Integer getOraclePort() {
        return Integer.parseInt(env.getProperty("spring.datasource.port"));
    }
}
