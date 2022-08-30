package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import com.vinorsoft.microservices.core.notarization.util.helpers.ClassHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper.SortSqlQuery;
import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Readable;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RepositoryBase<T> implements IRepository<T> {

    @Autowired
    protected IUnitOfWork unitOfWork;
    private final Class<T> type;

    public RepositoryBase(Class<T> type) {
        this.type = type;
    }

    @Override
    public Mono<Boolean> Add(T entity) throws SQLException {
        return AddOrUpdate(entity, "ADD");
    }

    @Override
    public Mono<Boolean> Update(T entity) throws SQLException {
        return AddOrUpdate(entity, "UPDATE");
    }

    private Mono<Boolean> AddOrUpdate(T entity, String typeQuery) throws SQLException {
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
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Statement statement = connection.createStatement(builder.toString());
                        try {
                            for (int i = 0; i < fields.length; i++) {
                                fields[i].setAccessible(true);
                                Object value = fields[i].get(entity);
                                Class<?> type = fields[i].getType();
                                if (value == null) {
                                    statement.bindNull(i, type);
                                } else if (type.equals(Date.class)) {
                                    Date utilDate = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
                                            .parse(value.toString());
                                    statement.bind(i,
                                            new java.sql.Date(utilDate.getTime()));
                                } else if (type.equals(Character.class)) {
                                    statement.bind(i, value.toString());
                                } else if (type.equals(Byte[].class)) {
                                    byte[] byteArray = DataHelper.SerializeObject(value);
                                    statement.bind(i, ByteBuffer.wrap(byteArray));
                                } else
                                    statement.bind(i, value);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Publisher<? extends Result> statementResult = statement.execute();
                        return Flux.from(statementResult)
                                .flatMap(Result::getRowsUpdated);
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .next()
                    .map(value -> {
                        return value > 0;
                    });
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Mono<Boolean> CheckContains(List<KeyValuePair<String, Object>> params)
            throws SQLException {
        return CountContain(params).map(value -> {
            return value > 0;
        });
    }

    @Override
    public Mono<Integer> Count() throws SQLException {
        String query = "SELECT COUNT(*) AS TOTAL FROM "
                + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName());
        return Flux.usingWhen(
                unitOfWork.getConnection().create(),
                connection -> {
                    Publisher<? extends Result> statementResult = connection.createStatement(query)
                            .execute();
                    return Flux.from(statementResult)
                            .flatMap(result -> result.map(row -> row.get(0, Integer.class)));
                },
                Connection::close)
                .doOnError(Throwable::printStackTrace)
                .next();
    }

    @Override
    public Mono<Integer> CountContain(List<KeyValuePair<String, Object>> params)
            throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_COUNTBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : params) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).append("?)}"); // with one out parameter
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Statement statement = connection.createStatement(builder.toString());
                        int i = 0;
                        for (KeyValuePair<String, Object> keyValuePair : params) {
                            statement.bind(i, keyValuePair.getValue());
                        }
                        statement.bind(params.size(), Parameters.out(R2dbcType.NUMERIC));
                        Publisher<? extends Result> statementResult = statement.execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> row.get(0, Integer.class)));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .next();
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Mono<Void> Delete(Long id) throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_DELETE(?)}";
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> connection.createStatement(query).bind(0, id).execute(),
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .then();
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Mono<Void> DeleteByStatus(Long id) throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName())
                + "_DELETEBY_STATUS(?)}";
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> connection.createStatement(query).bind(0, id).execute(),
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .then();
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Flux<T> GetAll() throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETALL()}";
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Publisher<? extends Result> statementResult = connection.createStatement(query).execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> {
                                    return MappingRowToObject(row, type);
                                }));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace);
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Flux<T> GetMultiByCondition(List<KeyValuePair<String, Object>> params)
            throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETMULTIBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : params) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).deleteCharAt(builder.length() - 1).append(")}");
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Statement statement = connection.createStatement(builder.toString());
                        int i = 0;
                        for (KeyValuePair<String, Object> keyValuePair : params) {
                            statement.bind(i++, keyValuePair.getValue());
                        }
                        Publisher<? extends Result> statementResult = statement.execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> {
                                    return MappingRowToObject(row, type);
                                }));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace);
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Flux<T> GetMultiPaging(String search, Integer page, Integer size, String orderby, SortSqlQuery sort)
            throws SQLException {
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
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Statement statement = connection.createStatement(builder.toString());
                        int i = 0;
                        for (KeyValuePair<String, Object> keyValuePair : params) {
                            statement.bind(i++, keyValuePair.getValue());
                        }
                        Publisher<? extends Result> statementResult = statement.execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> {
                                    return MappingRowToObject(row, type);
                                }));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace);
        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Mono<T> GetSingleByCondition(List<KeyValuePair<String, Object>> params)
            throws SQLException {
        StringBuilder builder = new StringBuilder(
                "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETSINGLEBY");
        String fields = "", conditision = "";
        for (KeyValuePair<String, Object> keyValuePair : params) {
            fields += "_" + keyValuePair.getKey().toUpperCase();
            conditision += "?,";
        }
        builder.append(fields + "(").append(conditision).deleteCharAt(builder.length() - 1).append(")}");
        try {
            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Statement statement = connection.createStatement(builder.toString());
                        int i = 0;
                        for (KeyValuePair<String, Object> keyValuePair : params) {
                            statement.bind(i++, keyValuePair.getValue());
                        }
                        Publisher<? extends Result> statementResult = statement.execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> {
                                    return MappingRowToObject(row, type);
                                }));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .next();

        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    @Override
    public Mono<T> GetSingleById(Object id) throws SQLException {
        String query = "{CALL " + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName()) + "_GETBYID(?)}";
        try {
            List<KeyValuePair<String, Object>> params = new ArrayList<KeyValuePair<String, Object>>();
            params.add(new KeyValuePair<String, Object>("ID", id));

            return Flux.usingWhen(
                    unitOfWork.getConnection().create(),
                    connection -> {
                        Publisher<? extends Result> statementResult = connection.createStatement(query)
                                .bind(0, id)
                                .execute();
                        return Flux.from(statementResult)
                                .flatMap(result -> result.map(row -> {
                                    return MappingRowToObject(row, type);
                                }));
                    },
                    Connection::close)
                    .doOnError(Throwable::printStackTrace)
                    .next();

        } catch (Exception ex) {
            throw new SQLException("Exception: " + ex.getMessage());
        }
    }

    private T MappingRowToObject(Readable row, Class<T> clazz) {
        T newObj = null;
        try {
            newObj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : ClassHelper.getAllFields(new LinkedList<Field>(), clazz)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                Object value = null;
                if (fieldType.equals(java.time.LocalDate.class)) {
                    field.set(newObj, row.get(fieldName, fieldType));
                } else if (fieldType.equals(Number.class)) {
                    NumberFormat format = NumberFormat.getInstance();
                    try {
                        value = format.parse(row.get(fieldName).toString());
                    } catch (ParseException e) {
                        throw new IllegalAccessException(
                                "Number class throw ParseException: " + e.getMessage());
                    }
                } else if (fieldType.equals(java.sql.Date.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd")
                            .parse(row.get(fieldName, fieldType).toString());
                    value = new java.sql.Date(utilDate.getTime());
                } else if (fieldType.equals(java.sql.Time.class)) {
                    Date utilDate = new SimpleDateFormat("HH:mm:ss")
                            .parse(row.get(fieldName, fieldType).toString());
                    value = new java.sql.Time(utilDate.getTime());
                } else if (fieldType.equals(java.sql.Timestamp.class)) {
                    Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                            .parse(row.get(fieldName, fieldType).toString());
                    value = new java.sql.Timestamp(utilDate.getTime());
                } else if (fieldType.equals(Byte[].class)) {
                    ByteBuffer buf = (ByteBuffer) row.get(fieldName);
                    byte[] bytes = new byte[buf.remaining()];
                    buf.get(bytes);
                    value = DataHelper.ToByteArray(bytes);
                } else if (fieldType.equals(Character.class)) {
                    value = row.get(fieldName).toString().charAt(0);
                } else
                    value = row.get(fieldName, fieldType);
                field.set(newObj, value);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception: " + ex.getMessage());
        }
        return newObj;
    }

    @Override
    public Mono<Void> DeleteAll() throws SQLException {
        String query = "DELETE FROM "
                + DataHelper.ConvertCamelCaseToUpperCase(this.type.getSimpleName());
        return Flux.usingWhen(
                unitOfWork.getConnection().create(),
                connection -> connection.createStatement(query).execute(),
                Connection::close)
                .doOnError(Throwable::printStackTrace)
                .then();
    }
}
