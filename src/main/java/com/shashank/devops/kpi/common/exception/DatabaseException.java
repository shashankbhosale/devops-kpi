package com.shashank.devops.kpi.common.exception;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException {
    public DatabaseException(SQLException e) {
        super(e);
    }
}
