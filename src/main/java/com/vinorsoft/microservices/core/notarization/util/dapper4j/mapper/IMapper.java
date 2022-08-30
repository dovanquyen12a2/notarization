package com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IMapper {
    public <T> T createObjectFromResultSet(ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException;
}
