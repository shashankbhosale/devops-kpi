package com.shashank.devops.kpi.processtime;

import com.shashank.devops.kpi.processtime.models.ProcessedEntity;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class ProcessDaoImpl extends HsqlDbManager {

    private static final String TABLE_NAME = "processed_entity";
    private static final String TABLE_CREATION_SQL = "CREATE TABLE IF NOT EXISTS "
            +TABLE_NAME + "(id INTEGER IDENTITY PRIMARY KEY, processId VARCHAR(50) NOT NULL, duration BIGINT, host VARCHAR(50), type VARCHAR(50), alert BOOLEAN DEFAULT FALSE NOT NULL)";


    public ProcessDaoImpl(){
        startHSQLDB();
        createProcessTableIfNotExists();
    }

    /**
     * Util method to drop the existing HSQL Table
     */
    public void dropProcessTable() {
        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();
            stmt.executeUpdate("DROP TABLE "+TABLE_NAME);
        }  catch (SQLException e) {
            log.error( e.getMessage(), e);
        }
    }

    /**
     * Creates the process table if not already existing
     */
    public void createProcessTableIfNotExists() {
        Statement stmt = null;
        try {
            //Check first if table exists
            DatabaseMetaData dbm = getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, new String[] {"TABLE"});

            if (!tables.next()) {
                stmt = getConnection().createStatement();
                stmt.executeUpdate(TABLE_CREATION_SQL);
                log.debug("Table "+TABLE_NAME+" created");
            } else{
                log.debug("Tables already exists");
            }
        }  catch (SQLException  e) {
            log.error( e.getMessage(), e);
        }
        if (log.isDebugEnabled()) {
            log.debug("createHSQLDBTable complete");
        }
    }

    /**
     * Inserts a single process entity record into the database
     * @param entity
     */
    public void insertRecord(ProcessedEntity entity) {
        if (log.isDebugEnabled()) {
            log.debug("New processed record to be inserted {}", entity);
        }
        Statement stmt = null;
        int changedRows = 0;

        try {
            stmt = getConnection().createStatement();
            //Prevent insertion if ID already exists:
            ResultSet existingRow = stmt.executeQuery("SELECT * FROM "+TABLE_NAME+" WHERE processId='"+entity.getProcessId()+"'");

            if (!existingRow.next()) {
                String sql = "INSERT INTO "+TABLE_NAME+" (processId, duration, host, type, alert) VALUES('"
                        + entity.getProcessId() +"',"
                        + entity.getDuration()+",'"
                        + entity.getHost()+"','"
                        + entity.getType()+"','"
                        + entity.raiseAlert() +"')";
                log.debug("sql statement ->  {}", sql);
                changedRows += stmt.executeUpdate(sql);

            } else {
                log.debug("Process ID already exists in table, updating record with new info. ");
                String sql = "UPDATE "+TABLE_NAME
                        +" set duration = "+ entity.getDuration() + ", host = '"+entity.getHost()+"', type='"+entity.getType()+"', alert='"+entity.raiseAlert() + "'" +
                        " WHERE processId='"+entity.getProcessId()+"' ";
                log.debug("sql statement ->  {}", sql);
                changedRows += stmt.executeUpdate(sql);
            }

            getConnection().commit();
        } catch (SQLException e) {
            log.error( e.getMessage(), e);
        }
        if (log.isDebugEnabled()) {
            log.debug("{} rows changed in the db", changedRows);
        }
    }

    /**
     * Returns a display-friendly string showing the content of the alerts table
     * @return elements of the alerts db to be displayed
     */
    public String readEvents() {
        StringBuilder sb  = new StringBuilder();
        Statement stmt = null;
        ResultSet result;

        try {
            stmt = getConnection().createStatement();
            result = stmt.executeQuery("SELECT * FROM "+TABLE_NAME);
            ResultSetMetaData rsmd = result.getMetaData();

            while (result.next()) {
                for (int i = 1; i <= 5; i++) {
                    if (i > 1) {
                        sb.append(",  ");
                    }
                    String columnValue = result.getString(i);
                    sb.append(rsmd.getColumnName(i)+":"+columnValue);
                }
                sb.append("\n");
            }
        } catch (SQLException e) {
            log.error( e.getMessage(), e);
        }
        return sb.toString();
    }


}
