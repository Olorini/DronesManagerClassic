package com.github.olorini.core;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AppContext implements ServletContextListener {
    private static Connection dbConnection;
    private static String realPath;
    private final Logger LOGGER = Logger.getLogger(AppContext.class);
    public static Connection getConnection() throws SQLException, IOException, ClassNotFoundException {
        if (dbConnection == null || dbConnection.isClosed()) {
            Properties properties = new Properties();
            try (InputStream in = AppContext.class.getResourceAsStream("/database.properties")) {
                properties.load(in);
            }
            String drivers = properties.getProperty("jdbc.drivers");
            if (drivers != null) {
                Class.forName(drivers);
            }
            String name = properties.getProperty("jdbc.name");
            String url;
            if ("built-in-h2".equals(name)) {
                String separator = FileSystems.getDefault().getSeparator();
                url = "jdbc:h2:" + realPath + properties.getProperty("jdbc.url").replace("/", separator);
            } else  {
                url = properties.getProperty("jdbc.url");
            }
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            dbConnection = DriverManager.getConnection(url, username, password);
        }
        return dbConnection;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        realPath = servletContext.getRealPath("/WEB-INF");
        try {
            getConnection();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }
}
