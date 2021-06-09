package com.shashank.devops.kpi.processtime;

import com.shashank.devops.kpi.common.exception.SleepThreadException;
import com.shashank.devops.kpi.processtime.models.ProcessedEntity;
import com.shashank.devops.kpi.processtime.models.EventLog;
import com.shashank.devops.kpi.processtime.models.EventParameters;
import com.shashank.devops.kpi.common.exception.ProcessingEventLogsFileException;
import com.shashank.devops.kpi.processtime.models.Output;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EventLogsFileProcessor {

    private com.shashank.devops.kpi.processtime.EventLogMapper eventLogMapper;
    private Map<String, EventLog> eventLogsMap;
    private List<ProcessedEntity> alerts;
    private ExecutorService executorService;
    private EventParameters eventParameters;
    private ProcessDaoImpl processDao;

    public EventLogsFileProcessor(EventParameters eventParameters) {
        this.eventParameters = eventParameters;
        eventLogsMap = new HashMap<>();
        alerts = new ArrayList<ProcessedEntity>(0);
        processDao = new ProcessDaoImpl();
        eventLogMapper = new com.shashank.devops.kpi.processtime.EventLogMapper();
    }

    public Output processEventLogsFile() {
        try {
            executorService = Executors.newFixedThreadPool(eventParameters.getNumberOfThreads());

            doProcessEventLogsFile(eventParameters.getFilePath());
            sleepThread();
            executorService.shutdown();
        } catch (IOException e) {
            //log.error("There was an error processing file. Cause: {}", e.getMessage());
            throw new ProcessingEventLogsFileException(e);
        }
        log.info("Processing of file completed.");

        //OUTPUT
        Output output = new Output(alerts, eventLogsMap);
        log.info("\n ---------------------------------------------------------------" +
                "\n Incomplete / Unprocessed events :\t {} |\n Processed events :\t\t\t {} |\n Events took more than threshold:\t {} |" +
                "\n -------------------------------------------------------------\n\n\n",
                output.getUnprocessedEventLogs(), output.getProcessedRecords(), output.getRecordsWithDurationMoreThanMs(new Long(eventParameters.getDurationThreshold())));

        return output;
    }

    private void doProcessEventLogsFile(String filePath) throws IOException {
        try (LineIterator it = FileUtils.lineIterator(new File(filePath), StandardCharsets.UTF_8.name())) {
            while (it.hasNext()) {
                processEventLogLine(it.nextLine());
            }
        }
    }

    private void processEventLogLine(String eventLogLine) {
        if (log.isDebugEnabled()) {
            log.debug("Processing Log Line {}", eventLogLine);
        }
        Optional<EventLog> eventLogOptional = eventLogMapper.mapEventLogLineToEventLogObject(eventLogLine);
        eventLogOptional.ifPresent(this::processEventLog);
    }

    private void processEventLog(EventLog eventLog) {
        if (log.isDebugEnabled()) {
            log.debug("Processing {}", eventLog);
        }
        if (eventLogsMap.containsKey(eventLog.getId())) {
            log.debug("Found log_id {}, inserting as alert record in db. ", eventLog.getId());
            processEvent(eventLog);
        } else {
            eventLogsMap.put(eventLog.getId(), eventLog);
        }
        if (log.isDebugEnabled()) {
            log.debug("eventLogsMap Size {}", eventLogsMap.size());
        }
    }


    private void processEvent(EventLog lastEventLog) {
        CompletableFuture.runAsync(() -> {
            //log.debug("ASYNC_START for {}", lastEventLog.getId());
            ProcessedEntity record = buildAlertRecord(lastEventLog);
            processDao.insertRecord(record);
            alerts.add(record);
            eventLogsMap.remove(lastEventLog.getId());
            //log.debug("ASYNC_END for {}", lastEventLog.getId());
        }, executorService);
    }

    private ProcessedEntity buildAlertRecord(EventLog eventLog) {
        EventLog startedLog = null;
        EventLog finishedLog = null;

        if (eventLog.getState().equals(EventLog.State.STARTED)) {
            startedLog = eventLog;
            finishedLog = eventLogsMap.get(eventLog.getId());
        } else {
            startedLog = eventLogsMap.get(eventLog.getId());
            finishedLog = eventLog;
        }
        return ProcessedEntity.newInstance(startedLog, finishedLog);
    }

    private void sleepThread() {
        try {
            //as we are inserting the records in async mode, sleep, so that db connection is not closed/shutdown in between
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("There was an error trying to sleep the main thread");
            throw new SleepThreadException(e);
        }
    }

}
