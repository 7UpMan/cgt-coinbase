package com.s4apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private Connection connect = null;

    public DbHelper() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://s4svr/cgt?user=cgt&password=h264dvv");
            connect.setAutoCommit(false);

        } catch (ClassNotFoundException ex) {
            LOGGER.error("Cannont connect database1.", ex);
            System.exit(1);
        } catch (SQLException ex) {
            LOGGER.error("Cannont connect database2.", ex);
            System.exit(1);
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
