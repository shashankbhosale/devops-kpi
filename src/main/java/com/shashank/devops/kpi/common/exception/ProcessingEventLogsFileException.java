package com.shashank.devops.kpi.common.exception;

import java.io.IOException;

public class ProcessingEventLogsFileException extends RuntimeException {
    public ProcessingEventLogsFileException(IOException e) {
        super(e);
    }
}
