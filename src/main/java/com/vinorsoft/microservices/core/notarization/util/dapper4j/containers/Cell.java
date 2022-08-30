package com.vinorsoft.microservices.core.notarization.util.dapper4j.containers;

import java.util.Map;

public class Cell implements ICell {
    private Map<String, Object> backingRow;

    public Cell(Map<String, Object> backingRow) {
        this.backingRow = backingRow;
    }

    @Override
    public Object obj(String column) {
        return backingRow.get(column.toLowerCase());
    }

    @Override
    public String str(String column) {
        return backingRow.get(column.toLowerCase()).toString();
    }
}
