package com.vinorsoft.microservices.core.notarization.util.dapper4j.parameter;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import com.vinorsoft.microservices.core.notarization.util.dapper4j.containers.Cell;

import java.time.LocalDate;

public class ParameterConsumer {
    public void consumeParameter(PreparedStatement ps, Collection<String> parameterNames, Object object)
            throws NoSuchFieldException, IllegalAccessException, SQLException {
        if (object instanceof Cell) {
            consumeCellParameter(ps, parameterNames, (Cell) object);
        } else {
            consumeObjectParameter(ps, parameterNames, object);
        }
    }

    private void consumeCellParameter(PreparedStatement ps, Collection<String> parameterNames, Cell dyno)
            throws SQLException {
        int i = 1;
        for (String parameterName : parameterNames) {
            // Prepared statement is 1-indexed
            ps.setObject(i, dyno.obj(parameterName));
            i++;
        }
    }

    private void consumeObjectParameter(PreparedStatement ps, Collection<String> parameterNames, Object object)
            throws NoSuchFieldException, IllegalAccessException, SQLException {
        int i = 1;

        Class<?> clazz = object.getClass();

        for (String parameterName : parameterNames) {
            Field field = clazz.getField(parameterName);

            // Prepared statement is 1-indexed
            if (LocalDate.class.isAssignableFrom(field.getType())) {
                java.sql.Date date = java.sql.Date.valueOf((LocalDate) field.get(object));
                ps.setDate(i, date);
            } else {
                ps.setObject(i, field.get(object));
            }
            i++;
        }
    }
}
