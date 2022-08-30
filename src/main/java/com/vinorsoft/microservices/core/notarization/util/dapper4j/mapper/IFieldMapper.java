package com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IFieldMapper {
    public void map(ResultSet resultSet, Object object) throws SQLException, IllegalAccessException;
}
