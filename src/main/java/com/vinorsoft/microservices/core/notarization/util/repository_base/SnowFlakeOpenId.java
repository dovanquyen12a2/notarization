package com.vinorsoft.microservices.core.notarization.util.repository_base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.vinorsoft.microservices.core.notarization.util.snowflake.Snowflake;

@Component
public class SnowFlakeOpenId implements IOpenId {

    @Autowired
    private Environment env;

    private Snowflake snowflake;

    @Override
    public Long GenerateID() {
        if (snowflake == null) {
            snowflake = new Snowflake(Long.parseLong(env.getProperty("snowflake.nodeid")));
        }
        return snowflake.nextId();
    }
}
