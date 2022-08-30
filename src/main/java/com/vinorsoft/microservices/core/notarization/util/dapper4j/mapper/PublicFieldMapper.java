package com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vinorsoft.microservices.core.notarization.util.helpers.ClassHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;

public class PublicFieldMapper implements IMapper {
    private List<IFieldMapper> mappers;

    // We are assuming that people will use public, writable fields that are exactly
    // the same as the column names
    public <T> T createObjectFromResultSet(ResultSet resultSet, Class<T> clazz)
            throws SQLException, IllegalAccessException, InstantiationException {
        if (mappers == null) {
            int colCount = resultSet.getMetaData().getColumnCount();

            Map<String, Integer> columnNames = new HashMap<>();

            for (int i = 0; i < colCount; i++) {
                // ResultSet metadata is 1-indexed
                columnNames.put(resultSet.getMetaData().getColumnName(i + 1).toLowerCase(), i);
            }

            mappers = new ArrayList<>();

            for (Field field : ClassHelper.getAllFields(new LinkedList<Field>(), clazz)) {
                field.setAccessible(true);
                String fieldName = field.getName().toLowerCase();

                if (columnNames.containsKey(fieldName)) {
                    final int index = columnNames.get(fieldName);

                    if (field.getType().equals(java.util.Date.class)) {
                        mappers.add(
                                (rs, obj) -> field.set(obj, new java.util.Date(rs.getTimestamp(index + 1).getTime())));
                    } else if (field.getType().equals(java.time.LocalDate.class)) {
                        mappers.add((rs, obj) -> field.set(obj, rs.getDate(index + 1).toLocalDate()));
                    } else {
                        try {
                            if (field.getType().equals(Boolean.class)) {
                                Boolean value = Integer.parseInt(resultSet.getObject(index + 1).toString()) == 1;
                                mappers.add((rs, obj) -> field.set(obj, value));
                            } else if (field.getType().equals(Integer.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Integer.parseInt(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Long.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Long.parseLong(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Short.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Short.parseShort(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Byte.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Byte.parseByte(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Double.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Double.parseDouble(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Float.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        Float.parseFloat(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(BigInteger.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        new BigInteger(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(BigDecimal.class)) {
                                mappers.add((rs, obj) -> field.set(obj,
                                        new BigDecimal(resultSet.getObject(index + 1).toString())));
                            } else if (field.getType().equals(Number.class)) {
                                NumberFormat format = NumberFormat.getInstance();
                                mappers.add((rs, obj) -> {
                                    try {
                                        field.set(obj,
                                                format.parse(resultSet.getObject(index + 1).toString()));
                                    } catch (ParseException e) {
                                        throw new IllegalAccessException(
                                                "Number class throw ParseException: " + e.getMessage());
                                    }
                                });
                            } else if (field.getType().equals(java.sql.Date.class)) {
                                Date utilDate = new SimpleDateFormat("yyyy-MM-dd")
                                        .parse(resultSet.getObject(index + 1).toString());
                                mappers.add((rs, obj) -> field.set(obj,
                                        new java.sql.Date(utilDate.getTime())));
                            } else if (field.getType().equals(java.sql.Time.class)) {
                                Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                        .parse(resultSet.getObject(index + 1).toString());
                                mappers.add((rs, obj) -> field.set(obj,
                                        new java.sql.Time(utilDate.getTime())));
                            } else if (field.getType().equals(java.sql.Timestamp.class)) {
                                Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                        .parse(resultSet.getObject(index + 1).toString());
                                mappers.add((rs, obj) -> field.set(obj,
                                        new java.sql.Timestamp(utilDate.getTime())));
                            } else if (field.getType().equals(Byte[].class)) {
                                mappers.add((rs, obj) -> {
                                    try {
                                        byte[] bytes;
                                        bytes = DataHelper.SerializeObject(resultSet.getObject(index + 1));
                                        Byte[] byteObjects = new Byte[bytes.length];
                                        int i = 0;
                                        for (byte b : bytes)
                                            byteObjects[i++] = b;
                                        field.set(obj, byteObjects);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else if (field.getType().equals(Character.class)) {
                                mappers.add(
                                        (rs, obj) -> field.set(obj, rs.getObject(index + 1).toString().charAt(0)));
                            } else
                                mappers.add((rs, obj) -> field.set(obj, rs.getObject(index + 1)));
                        } catch (Exception ex) {
                            throw new IllegalAccessException("ParseException: " + ex.getMessage());
                        }
                    }
                }
            }
        }

        try {
            T newObj = clazz.getDeclaredConstructor().newInstance();

            for (IFieldMapper mapper : mappers) {
                mapper.map(resultSet, newObj);
            }
            return newObj;
        } catch (Exception e) {
            throw new InstantiationException();
        }
    }
}
