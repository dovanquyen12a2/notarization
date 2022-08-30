package com.vinorsoft.microservices.core.notarization.util.dapper4j.containers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper.IMapper;

public class LazyResultSetCollection<E> implements Collection<E> {
    private ResultSet rs;
    private IMapper mapper;
    private Class<E> clazz;

    public LazyResultSetCollection(ResultSet resultSet, IMapper mapper, Class<E> clazz) {
        rs = resultSet;
        this.mapper = mapper;
        this.clazz = clazz;
    }

    @Override
    public Iterator<E> iterator() {
        return new ResultSetIterator<>(rs, clazz);
    }

    @Override
    public Object[] toArray() {
        List<E> results = new ArrayList<>();

        this.stream().forEach(results::add);

        return results.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<E> results = new ArrayList<>();

        this.stream().forEach(results::add);

        return results.toArray(a);
    }

    @Override
    public void clear() {
    }

    private class ResultSetIterator<C> implements Iterator<C> {
        private ResultSet rs;
        private C next;
        private Class<C> clazz;

        public ResultSetIterator(ResultSet resultSet, Class<C> clazz) {
            rs = resultSet;
            this.clazz = clazz;
        }

        @Override
        public boolean hasNext() {
            if (next == null) {
                boolean hasNext = false;
                try {
                    if (hasNext = rs.next()) {
                        next = mapper.createObjectFromResultSet(rs, clazz);
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                }

                return hasNext;
            } else {

                return true;
            }
        }

        @Override
        public C next() {
            if (next == null) {
                try {
                    rs.next();
                    return mapper.createObjectFromResultSet(rs, clazz);
                } catch (Exception e) {
                    return null;
                }
            } else {
                C returnItem = next;
                next = null;
                return returnItem;
            }
        }
    }

    @Override
    public boolean add(E arg0) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> arg0) {
        return false;
    }

    @Override
    public boolean contains(Object arg0) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean remove(Object arg0) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }
}
