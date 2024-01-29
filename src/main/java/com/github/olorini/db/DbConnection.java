package com.github.olorini.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private Connection dbConnection;

    public Connection getConnection() throws SQLException, IOException, ClassNotFoundException {
        if (dbConnection == null) {
            Properties properties = new Properties();
            try (InputStream in = getClass().getResourceAsStream("/database.properties")) {
                properties.load(in);
            }
            String drivers = properties.getProperty("jdbc.drivers");
            if (drivers != null) {
                //System.setProperty("jdbc.drivers", drivers);
                Class.forName(drivers);
            }
            String url = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            dbConnection = DriverManager.getConnection(url, username, password);
        }
        return dbConnection;
    }
}
