package com.github.olorini.db;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DronesDboRepository {

    private final DbConnection connection;

    public DronesDboRepository() {
        this.connection = new DbConnection();
    }

    public List<DroneEntity> findDrones() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = connection.getConnection();
        Statement stat = conn.createStatement();
        List<DroneEntity> result = new ArrayList<>();
//        DatabaseMetaData md = conn.getMetaData();
//        ResultSet rs = md.getTables(null, "PUBLIC", "%", null);
//        while (rs.next()) {
//            System.out.println(rs.getString(3));
//        }
//        ResultSet rs2 = conn.getMetaData().getCatalogs();
//        while (rs2.next()) {
//            System.out.println("TABLE_CAT = " + rs2.getString("TABLE_CAT") );
//        }
        try (ResultSet resultSet = stat.executeQuery("select * from DRONES")) {
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            System.out.println("No. of columns : " + rsmd.getColumnCount());
//            System.out.println("Column name of 1st column : " + rsmd.getColumnName(1));
//            System.out.println("Column type of 1st column : " + rsmd.getColumnTypeName(1));
            while (resultSet.next()) {
                result.add(new DroneEntity(resultSet));
            }
        }
        conn.close();
        return result;
    }
}
