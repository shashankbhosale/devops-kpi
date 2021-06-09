package com.shashank.devops.kpi.common.exception;

public class SleepThreadException extends RuntimeException {
    public SleepThreadException(InterruptedException e) {
        super(e);
    }
}
