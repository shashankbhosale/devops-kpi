package com.shashank.devops.kpi.processtime.models;

import lombok.Value;

@Value
public class EventParameters {

    private final String filePath;
    private final int numberOfThreads;
    private final int durationThreshold;


    private EventParameters(String filePath, int numberOfThreads, int durationThreshold) {
        this.filePath = filePath;
        this.numberOfThreads = numberOfThreads;
        this.durationThreshold = durationThreshold;
    }

    public static EventParameters newInstance(String filePath, int numberOfThreads, int durationThreshold) {
        return new EventParameters(filePath, numberOfThreads, durationThreshold);
    }

}
