package com.shashank.devops.kpi.processtime.models;

import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Output {

    private List<ProcessedEntity> processedRecords;
    private Map<String, EventLog> unprocessedEventLogs;

    public Output(List<ProcessedEntity> processedRecords, Map<String, EventLog> unprocessedEventLogs){
        this.processedRecords = processedRecords;
        this.unprocessedEventLogs = unprocessedEventLogs;
    }

    public List<ProcessedEntity> getRecordsWithDurationMoreThanMs(Long milliSecDuration){
        List<ProcessedEntity> filteredList = processedRecords.stream().filter(
                rec -> rec.getDuration() >= milliSecDuration
        ).collect(Collectors.toList());
        return filteredList;
    }

    public void export(){
        //TODO export into a report file

    }


    public List<ProcessedEntity> getProcessedRecords() {
        return processedRecords;
    }

    public Map<String, EventLog> getUnprocessedEventLogs() {
        return unprocessedEventLogs;
    }
}
