package com.shashank.devops.kpi.processtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashank.devops.kpi.processtime.models.EventLog;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class EventLogMapper {

    private ObjectMapper objectMapper;

    public EventLogMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public Optional<EventLog> mapEventLogLineToEventLogObject(String eventLogLine) {
        try {
            return Optional.of(
                    objectMapper.readValue(eventLogLine, EventLog.class)
            );
        } catch (IOException e) {
            log.error("There was an error mapping the event log line: {}", eventLogLine, e);
            return Optional.empty();
        }
    }
}
