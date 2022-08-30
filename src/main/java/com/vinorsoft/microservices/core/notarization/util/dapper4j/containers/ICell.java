package com.vinorsoft.microservices.core.notarization.util.dapper4j.containers;

public interface ICell {
    Object obj(String column);

    String str(String column);
}
