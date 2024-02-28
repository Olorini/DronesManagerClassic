package com.github.olorini.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringJoiner;

public class DboTools {

    public static String getIn(String fieldName, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ").append(fieldName).append(" IN (");
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < size; i++) {
            joiner.add("?");
        }
        sb.append(joiner).append(")");
        return sb.toString();
    }

    public static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            for (int x = 1; x <= columns; x++) {
                if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }
}
