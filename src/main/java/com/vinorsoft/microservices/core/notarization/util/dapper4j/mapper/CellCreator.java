package com.vinorsoft.microservices.core.notarization.util.dapper4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.vinorsoft.microservices.core.notarization.util.dapper4j.containers.*;

public class CellCreator {
    private ResultSet resultSet;
    private String[] columnNames;

    public CellCreator(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public ICell createCell() throws SQLException {
        if (columnNames == null) {
            int colCount = resultSet.getMetaData().getColumnCount();

            columnNames = new String[colCount];

            for (int i = 0; i < colCount; i++) {
                // Column metadata is 1-indexed
                columnNames[i] = resultSet.getMetaData().getColumnName(i + 1).toLowerCase();
            }
        }

        Map<String, Object> backingRow = new HashMap<>();

        for (int i = 0; i < columnNames.length; i++) {
            // ResultSet is 1-indexed, not 0-indexed
            backingRow.put(columnNames[i], resultSet.getObject(i + 1));
        }

        return new Cell(backingRow);
    }
}
