package com.shashank.devops.kpi.processtime.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Value;

@Data
public class EventLog {

    public enum State {
        STARTED,
        FINISHED
    }

    private final String id;
    private final State state;
    private final long timestamp;

    @JsonIgnoreProperties
    private final String type;

    @JsonIgnoreProperties
    private final String host;

    @JsonCreator
    public EventLog(@JsonProperty("id") String id,
                    @JsonProperty("state") State state,
                    @JsonProperty("timestamp") long timestamp,
                    @JsonProperty("type") String type,
                    @JsonProperty("host") String host) {

        this.id = id;
        this.state = state;
        this.timestamp = timestamp;
        this.type = type;
        this.host = host;
    }

    public EventLog(String id,
                    State state,
                    Long timestamp) {

        this.id = id;
        this.state = state;
        this.timestamp = timestamp;
        this.type = "";
        this.host = "";
    }


}
