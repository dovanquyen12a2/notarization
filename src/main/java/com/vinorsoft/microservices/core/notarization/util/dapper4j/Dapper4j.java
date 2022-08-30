package com.vinorsoft.microservices.core.notarization.util.dapper4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vinorsoft.microservices.core.notarization.util.dapper4j.containers.*;
import com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper.*;
import com.vinorsoft.microservices.core.notarization.util.dapper4j.parameter.*;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

public class Dapper4j {
    private Connection connection;
    private IMapperGenerator mapperGenerator;
    private ParameterConsumer parameterConsumer;
    private Parameterizer parameterizer;

    public Dapper4j(Connection connection) {
        this.connection = connection;
        this.mapperGenerator = PublicFieldMapper::new;
        this.parameterConsumer = new ParameterConsumer();
        this.parameterizer = new Parameterizer();
    }

    public <T> Collection<T> queryStoreProcedure(String sql, List<KeyValuePair<String, Object>> params, Class<T> clazz)
            throws SQLException, ParseException, InstantiationException, IllegalAccessException, IOException {
        return queryStoreProcedure(sql, params, clazz, false, true);
    }

    public <T> Collection<T> queryStoreProcedure(String sql, List<KeyValuePair<String, Object>> params, Class<T> clazz,
            boolean isCountStoreProcedure, boolean buffered)
            throws SQLException, ParseException, InstantiationException, IllegalAccessException, IOException {
        CallableStatement statement = connection.prepareCall(sql);

        if (params != null) {
            int i = 0;
            for (KeyValuePair<String, Object> keyValuePair : params) {
                Object value = keyValuePair.getValue();
                if (value.getClass().equals(String.class)) {
                    statement.setString(i + 1, value.toString());
                } else if (value.getClass().equals(Boolean.class)) {
                    statement.setBoolean(i + 1, Boolean.parseBoolean(value.toString()));
                } else if (value.getClass().equals(Integer.class)) {
                    statement.setInt(i + 1, Integer.parseInt(value.toString()));
                } else if (value.getClass().equals(Long.class)) {
                    statement.setLong(i + 1, Long.parseLong(value.toString()));
                } else if (value.getClass().equals(Short.class)) {
                    statement.setShort(i + 1, Short.parseShort(value.toString()));
                } else if (value.getClass().equals(Byte.class)) {
                    statement.setByte(i + 1, Byte.parseByte(value.toString()));
                } else if (value.getClass().equals(Double.class)) {
                    statement.setDouble(i + 1, Double.parseDouble(value.toString()));
                } else if (value.getClass().equals(Float.class)) {
                    statement.setFloat(i + 1, Float.parseFloat(value.toString()));
                } else if (value.getClass().equals(Date.class)) {
                    Date utilDate = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(value.toString());
                    statement.setDate(i + 1,
                            new java.sql.Date(utilDate.getTime()));
                } else if (value.getClass().equals(BigInteger.class)) {
                    statement.setBigDecimal(i + 1, new BigDecimal(value.toString()));
                } else if (value.getClass().equals(BigDecimal.class)) {
                    statement.setBigDecimal(i + 1, new BigDecimal(value.toString()));
                } else if (value.getClass().equals(Number.class)) {
                    statement.setBigDecimal(i + 1, new BigDecimal(Long.parseLong(value.toString())));
                } else if (value.getClass().equals(java.sql.Date.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                    statement.setDate(i + 1,
                            new java.sql.Date(utilDate.getTime()));
                } else if (value.getClass().equals(java.sql.Time.class)) {
                    Date utilDate = new SimpleDateFormat("HH:mm:ss").parse(value.toString());
                    statement.setTime(i + 1,
                            new java.sql.Time(utilDate.getTime()));
                } else if (value.getClass().equals(java.sql.Timestamp.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value.toString());
                    statement.setTimestamp(i + 1,
                            new java.sql.Timestamp(utilDate.getTime()));
                } else if (value.getClass().equals(Byte[].class)) {
                    byte[] byteArray = DataHelper.SerializeObject(value);
                    statement.setBinaryStream(i + 1, new ByteArrayInputStream(byteArray));
                } else if (value.getClass().equals(Character.class)) {
                    statement.setString(i + 1, value.toString());
                } else {
                    throw new IllegalArgumentException("Field type isn't processed!");
                }
                i++;
            }
        }

        // Add one out parameter for get count()
        if (isCountStoreProcedure) {
            statement.registerOutParameter(params.size() + 1, Types.INTEGER);
            statement.executeUpdate();

            Integer count = statement.getInt(params.size() + 1);
            try {
                List<T> results = new ArrayList<>();
                results.add((T) count);

                return results;
            } catch (Exception e) {
                throw new InstantiationException();
            }
        }

        ResultSet rs;
        Boolean result = statement.execute();
        if (result)
            rs = statement.executeQuery();
        else {
            result = statement.getMoreResults();
            rs = statement.getResultSet();
        }
        IMapper mapper = mapperGenerator.getInstance();
        if (buffered) {
            List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(mapper.createObjectFromResultSet(rs, clazz));
            }
            return results;
        } else {
            return new LazyResultSetCollection<>(rs, mapper, clazz);
        }
    }

    public Collection<ICell> query(String sql, boolean buffered)
            throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(sql);

        if (buffered) {
            List<ICell> ICells = new ArrayList<>();

            CellCreator creator = new CellCreator(rs);

            while (rs.next()) {
                ICells.add(creator.createCell());
            }

            return ICells;
        } else {
            return new LazyResultSetCollection<>(rs, new CellMapper(), ICell.class);
        }
    }

    public Collection<ICell> query(String sql)
            throws SQLException {
        return query(sql, true);
    }

    public <T> Collection<T> query(String sql, Class<T> clazz, boolean buffered)
            throws SQLException, InstantiationException, IllegalAccessException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(sql);

        IMapper mapper = mapperGenerator.getInstance();

        if (buffered) {
            List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(mapper.createObjectFromResultSet(rs, clazz));
            }

            return results;
        } else {
            return new LazyResultSetCollection<>(rs, mapper, clazz);
        }
    }

    public <T> Collection<T> query(String sql, Class<T> clazz)
            throws SQLException, InstantiationException, IllegalAccessException {
        return query(sql, clazz, true);
    }

    public <T> Collection<T> query(String sql, Class<T> clazz, Object param)
            throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        return query(sql, clazz, param, true);
    }

    public <T> Collection<T> query(String sql, Class<T> clazz, Object param, boolean buffered)
            throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {

        IMapper mapper = mapperGenerator.getInstance();

        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);

        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);

        ResultSet rs = ps.executeQuery();

        if (buffered) {
            List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(mapper.createObjectFromResultSet(rs, clazz));
            }

            return results;
        } else {
            return new LazyResultSetCollection<>(rs, mapper, clazz);
        }
    }

    public int execute(String sql) throws SQLException {
        Statement statement = connection.createStatement();

        return statement.executeUpdate(sql);
    }

    public int executeBatch(String sql, Collection params)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);
        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        for (Object param : params) {
            parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);
            ps.addBatch();
        }

        int[] counts = ps.executeBatch();

        int sum = 0;

        for (int count : counts) {
            sum += count;
        }

        return sum;
    }

    public int execute(String sql, Object param) throws SQLException, NoSuchFieldException, IllegalAccessException {
        ParameterizedSql parameterizedSql = parameterizer.parameterizeSql(sql);
        PreparedStatement ps = connection.prepareStatement(parameterizedSql.sql);

        parameterConsumer.consumeParameter(ps, parameterizedSql.parameterNames, param);

        return ps.executeUpdate();
    }
}
