package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vinorsoft.microservices.core.notarization.util.dapper4j.Dapper4j;
import com.vinorsoft.microservices.core.notarization.util.helpers.ClassHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

public class RepositoryBase<T> implements IRepository<T> {

    @Autowired
    protected IUnitOfWork unitOfWork;
    private final Class<T> type;

    public RepositoryBase(Class<T> type) {
        this.type = type;
    }

    @Override
    public Boolean CheckContains(List<KeyValuePair<String, Object>> param) throws SQLException {
        return CountContain(param) > 0;
    }

    @Override
    public Integer Count() throws SQLException {
        String query = "SELECT COUNT(*) AS TOTAL FROM "
                + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName());
        Statement statement = unitOfWork.getConnection().createStatement();
        ResultSet result = statement.executeQuery(query);
        result.next();
        return result.getInt("TOTAL");
    }

    @Override
    public Integer CountContain(List<KeyValuePair<String, Object>> param) throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_COUNTBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : param) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).append("?)}"); // with one out parameter
        try {
            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(builder.toString(), param, Integer.class, true, true).stream().findFirst()
                    .get();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void Delete(Long id) throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_DELETE(?)}";
        unitOfWork.getConnection().setAutoCommit(false);
        CallableStatement stmt = unitOfWork.getConnection().prepareCall(query);
        stmt.setLong(1, id);
        stmt.execute();
    }

    @Override
    public void DeleteByStatus(Long id) throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName())
                + "_DELETEBY_STATUS(?)}";
        unitOfWork.getConnection().setAutoCommit(false);
        CallableStatement stmt = unitOfWork.getConnection().prepareCall(query);
        stmt.setLong(1, id);
        stmt.execute();
    }

    @Override
    public Collection<T> GetMultiByCondition(List<KeyValuePair<String, Object>> param) throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETMULTIBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : param) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).deleteCharAt(builder.length() - 1).append(")}");
        try {
            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(builder.toString(), param, this.type);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Collection<T> GetMultiPaging(String search, Integer page, Integer size, String orderby,
            DataHelper.SortSqlQuery sort)
            throws SQLException, IllegalArgumentException {
        if (search == null)
            search = "";
        if (orderby == null)
            orderby = "";
        if (sort == null)
            sort = DataHelper.SortSqlQuery.ASC;
        if (page < 0 || size < 0)
            throw new IllegalArgumentException("page or size must be greater than 0");

        if (orderby != null) {
            Field[] fields = ClassHelper.getAllFields(new LinkedList<Field>(), type).toArray(Field[]::new);
            Boolean hasField = false;
            for (Field field : fields) {
                if (field.getName().equals(orderby)) {
                    hasField = true;
                    break;
                }
            }
            if (!hasField)
                throw new IllegalArgumentException("Field " + orderby + " not found on class " + type.getSimpleName());
        }

        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName())
                        + "_GETMULTIBY_PAGING(?,?,?,?,?)");
        List<KeyValuePair<String, Object>> params = new ArrayList<KeyValuePair<String, Object>>();
        params.add(new KeyValuePair<String, Object>("SEARCH", search));
        params.add(new KeyValuePair<String, Object>("PAGE", page));
        params.add(new KeyValuePair<String, Object>("SIZE", size));
        params.add(new KeyValuePair<String, Object>("ORDERBY", orderby));
        params.add(new KeyValuePair<String, Object>("SORT", sort.toString()));
        try {
            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(builder.toString(), params, this.type);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public T GetSingleByCondition(List<KeyValuePair<String, Object>> param) throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETSINGLEBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : param) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).deleteCharAt(builder.length() - 1).append(")}");
        try {
            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(builder.toString(), param, this.type).stream().findFirst().get();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public T GetSingleById(Object id) throws SQLException, IllegalArgumentException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETBYID(?)}";
        try {
            List<KeyValuePair<String, Object>> params = new ArrayList<KeyValuePair<String, Object>>();
            params.add(new KeyValuePair<String, Object>("ID", id));

            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(query, params, this.type).stream().findFirst().get();
        } catch (ParseException ex) {
            throw new IllegalArgumentException("ParseException: " + ex.getMessage());
        } catch (NoSuchElementException ex) {
            return null;
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("IllegalAccessException: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IllegalArgumentException("IOException: " + ex.getMessage());
        }
    }

    @Override
    public Boolean Add(T entity) throws SQLException, IllegalArgumentException {
        return AddOrUpdate(entity, "ADD");
    }

    @Override
    public Boolean Update(T entity) throws SQLException, IllegalArgumentException {
        return AddOrUpdate(entity, "UPDATE");
    }

    private Boolean AddOrUpdate(T entity, String typeQuery) throws IllegalArgumentException, SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_" + typeQuery
                + "(";
        StringBuilder builder = new StringBuilder(query);
        Field[] fields = ClassHelper.getAllFields(new LinkedList<Field>(), type).toArray(Field[]::new);
        IntStream.range(0, fields.length).forEachOrdered(n -> {
            builder.append("?,");
        });

        builder.deleteCharAt(builder.length() - 1);
        builder.append(")}");
        try {
            unitOfWork.getConnection().setAutoCommit(false);
            CallableStatement stmt = unitOfWork.getConnection().prepareCall(builder.toString());
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(entity);
                if (fields[i].getType().equals(String.class)) {
                    stmt.registerOutParameter(i + 1, Types.VARCHAR);
                    stmt.setString(i + 1, value.toString());
                } else if (fields[i].getType().equals(Boolean.class)) {
                    stmt.setBoolean(i + 1, Boolean.parseBoolean(value.toString()));
                } else if (fields[i].getType().equals(Integer.class)) {
                    stmt.setInt(i + 1, Integer.parseInt(value.toString()));
                } else if (fields[i].getType().equals(Long.class)) {
                    stmt.setLong(i + 1, Long.parseLong(value.toString()));
                } else if (fields[i].getType().equals(Short.class)) {
                    stmt.setShort(i + 1, Short.parseShort(value.toString()));
                } else if (fields[i].getType().equals(Byte.class)) {
                    stmt.setByte(i + 1, Byte.parseByte(value.toString()));
                } else if (fields[i].getType().equals(Double.class)) {
                    stmt.setDouble(i + 1, Double.parseDouble(value.toString()));
                } else if (fields[i].getType().equals(Float.class)) {
                    stmt.setFloat(i + 1, Float.parseFloat(value.toString()));
                } else if (fields[i].getType().equals(Date.class)) {
                    Date utilDate = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(value.toString());
                    stmt.setDate(i + 1,
                            new java.sql.Date(utilDate.getTime()));
                } else if (fields[i].getType().equals(BigInteger.class)) {
                    stmt.setBigDecimal(i + 1, new BigDecimal(value.toString()));
                } else if (fields[i].getType().equals(BigDecimal.class)) {
                    stmt.setBigDecimal(i + 1, new BigDecimal(value.toString()));
                } else if (fields[i].getType().equals(Number.class)) {
                    stmt.setBigDecimal(i + 1, new BigDecimal(Long.parseLong(value.toString())));
                } else if (fields[i].getType().equals(java.sql.Date.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                    stmt.registerOutParameter(i + 1, Types.DATE);
                    stmt.setDate(i + 1,
                            new java.sql.Date(utilDate.getTime()));
                } else if (fields[i].getType().equals(java.sql.Time.class)) {
                    Date utilDate = new SimpleDateFormat("HH:mm:ss").parse(value.toString());
                    stmt.registerOutParameter(i + 1, Types.TIME);
                    stmt.setTime(i + 1,
                            new java.sql.Time(utilDate.getTime()));
                } else if (fields[i].getType().equals(java.sql.Timestamp.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value.toString());
                    stmt.registerOutParameter(i + 1, Types.TIMESTAMP);
                    stmt.setTimestamp(i + 1,
                            new java.sql.Timestamp(utilDate.getTime()));
                } else if (fields[i].getType().equals(Byte[].class)) {
                    byte[] byteArray = DataHelper.SerializeObject(value);
                    stmt.setBinaryStream(i + 1, new ByteArrayInputStream(byteArray));
                } else if (fields[i].getType().equals(Character.class)) {
                    stmt.registerOutParameter(i + 1, Types.CHAR);
                    stmt.setString(i + 1, value.toString());
                } else {
                    throw new IllegalArgumentException("Field type isn't processed!");
                }
            }
            return stmt.executeUpdate() > 0;
        } catch (ParseException ex) {
            throw new IllegalArgumentException("ParseException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("IllegalAccessException: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IllegalArgumentException("IOException: " + ex.getMessage());
        }
    }

    @Override
    public Collection<T> GetAll() throws SQLException, IllegalArgumentException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETALL()}";
        try {
            Dapper4j dapper = new Dapper4j(unitOfWork.getConnection());
            return dapper.queryStoreProcedure(query, null, this.type);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("ParseException: " + ex.getMessage());
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("IllegalAccessException: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IllegalArgumentException("IOException: " + ex.getMessage());
        }
    }

}
