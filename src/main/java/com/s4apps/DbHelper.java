package com.s4apps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PROPERTIES_FILE = "config/db.properties";

    private Connection connect = null;

    public DbHelper() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = loadProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            if (url == null || url.trim().isEmpty() || user == null || user.trim().isEmpty() || password == null) {
                throw new IllegalArgumentException("Database configuration is incomplete. Check " + PROPERTIES_FILE);
            }

            // Setup the connection with the DB
            connect = DriverManager.getConnection(url, user, password);
            connect.setAutoCommit(false);

        } catch (ClassNotFoundException ex) {
            LOGGER.error("Cannont connect database1.", ex);
            System.exit(1);
        } catch (IOException | SQLException | IllegalArgumentException ex) {
            LOGGER.error("Cannont connect database2.", ex);
            System.exit(1);
        }

    }

    private Properties loadProperties() throws IOException {
        Path path = Paths.get(PROPERTIES_FILE);
        Properties props = new Properties();

        if (Files.exists(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                props.load(input);
                return props;
            }
        }

        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
        if (resourceStream == null) {
            throw new IOException("Unable to find database configuration. Expected " + PROPERTIES_FILE + " or a classpath db.properties file.");
        }

        try (InputStream input = resourceStream) {
            props.load(input);
            return props;
        }
    }

    public void commit() {
        try {
            connect.commit();
        } catch (SQLException e) {
            LOGGER.error("Cannontcommit transaction.", e);
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return connect;
    }

    public void close() {

        try {
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannont close database2.", e);
        }
    }

}
