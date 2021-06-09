package com.shashank.devops.kpi.processtime;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.extern.slf4j.Slf4j;
import org.hsqldb.Server;

@Slf4j
public abstract class HsqlDbManager {

    private static final String DB_NAME = "devops.kpi";
    private static final String DB_PATH = "file:devops.kpi";
    private static final String HOSTNAME = "localhost";


    private static Server hsqlServer = new Server();
    private Connection connection;


    public String getDbName(){
        return DB_NAME;
    }

    public String getDbPath() {
        return DB_PATH;
    }

    public String getHostname() {
        return HOSTNAME;
    }


    /**
     * Starts the HSQL DB Server directly from the application
     */
    public void startHSQLDB() {
        if (log.isDebugEnabled()) {
            log.debug("Starting HSQL server");
        }

        if(hsqlServer.isNotRunning()) {
            hsqlServer.setLogWriter(null);
            hsqlServer.setSilent(true);

            hsqlServer.setDatabaseName(0, getDbName());
            hsqlServer.setDatabasePath(0, getDbPath());

            Logger.getLogger("hsqldb.db").setLevel(Level.WARNING);
            System.setProperty("hsqldb.reconfig_logging", "false");
            hsqlServer.start();

            if (log.isDebugEnabled()) {
                log.debug("HSQL server started.");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("HSQL server already running.");
            }
        }
    }

    /**
     * Closes the existing connection and stops the HSQLDB server
     */
    public void stopHSQLDB() {
        if (log.isDebugEnabled()) {
            log.debug("Stopping HSQL server");
        }
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to HSQL Server closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        hsqlServer.stop();
        if (log.isDebugEnabled()) {
            log.debug("HSQL server stopped");
        }
    }

    /**
     * Util method to create a connection
     * @return opened connection to the db
     */
    private Connection openConnection() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            return DriverManager.getConnection("jdbc:hsqldb:hsql://"+getHostname()+"/"+getDbName(), "SA", "");
        } catch (SQLException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Connection getConnection(){
        if (connection == null) {
            connection = openConnection();
        }
        return connection;
    }

}
