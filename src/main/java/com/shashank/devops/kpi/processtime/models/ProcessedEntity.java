package com.shashank.devops.kpi.processtime.models;

import lombok.Value;

@Value
public class ProcessedEntity {

    private String processId;
    private Long duration;
    private String host;
    private String type;

    private ProcessedEntity(String processId, Long duration, String host, String type) {
        this.processId = processId;
        this.duration = duration;
        this.host = host;
        this.type = type;
    }

    public static ProcessedEntity newInstance(EventLog startEventLog, EventLog finishEventLog){
        return new ProcessedEntity(startEventLog.getId(),
                finishEventLog.getTimestamp() - startEventLog.getTimestamp(),
                startEventLog.getHost(),
                startEventLog.getType()
        );
    }

    public boolean raiseAlert() {
        return this.duration >= 4;
    }

}
