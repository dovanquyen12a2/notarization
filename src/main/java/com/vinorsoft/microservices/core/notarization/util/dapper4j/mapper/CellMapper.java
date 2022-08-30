package com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CellMapper implements IMapper {
    private CellCreator cellCreator;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createObjectFromResultSet(ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException {
        if (cellCreator == null) {
            cellCreator = new CellCreator(resultSet);
        }
        return (T) cellCreator.createCell();
    }
}
