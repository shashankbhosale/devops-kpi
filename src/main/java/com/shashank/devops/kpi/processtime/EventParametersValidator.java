package com.shashank.devops.kpi.processtime;

import com.shashank.devops.kpi.processtime.models.EventParameters;
import com.shashank.devops.kpi.common.exception.FilePathParameterMissingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventParametersValidator {

    public static final int DEFAULT_THREAD_POOL_SIZE = 1;
    public static final int DEFAULT_DURATION_THRESHOLD = 4;

    private static final int FILE_PATH_PARAMETER_INDEX = 0;
    private static final int NUMBER_OF_THREADS_PARAMETER_INDEX = 1;
    private static final int DURATION_THRESHOLD_INDEX = 2;

    private final String[] vars;

    public EventParametersValidator(String... vars) {
        this.vars = vars.clone();
    }

    public EventParameters getEventParameters() {
        return EventParameters.newInstance(getFilePath(), getNumberOfThreadsParameter(), getDurationThresholdParameter());
    }

    private String getFilePath() {
        try {
            return vars[FILE_PATH_PARAMETER_INDEX];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("File path parameter is missing");
            throw new FilePathParameterMissingException(e);
        }
    }

    private int getNumberOfThreadsParameter() {
        try {
            return Integer.valueOf(vars[NUMBER_OF_THREADS_PARAMETER_INDEX]);
        } catch (NumberFormatException e) {
            log.info("{} should be a number", vars[NUMBER_OF_THREADS_PARAMETER_INDEX]);
            log.info("Using Default thread pool size: {}", DEFAULT_THREAD_POOL_SIZE);
            return DEFAULT_THREAD_POOL_SIZE;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.info("Default thread pool size: {}", DEFAULT_THREAD_POOL_SIZE);
            return DEFAULT_THREAD_POOL_SIZE;
        }
    }

    private int getDurationThresholdParameter() {
        try {
            return Integer.valueOf(vars[DURATION_THRESHOLD_INDEX]);
        } catch (NumberFormatException e) {
            log.info("{} should be a number", vars[DURATION_THRESHOLD_INDEX]);
            log.info("Using default threshold : {}", DEFAULT_DURATION_THRESHOLD);
            return DEFAULT_DURATION_THRESHOLD;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.info("Using default threshold : {}", DEFAULT_DURATION_THRESHOLD);
            return DEFAULT_DURATION_THRESHOLD;
        }
    }
}
