package cpm.shashank.devops.kpi.buildtime;


import com.shashank.devops.kpi.processtime.EventLogsFileProcessor;
import com.shashank.devops.kpi.processtime.models.EventParameters;
import com.shashank.devops.kpi.processtime.models.Output;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;

@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventLogsFileProcessor.class)
public class EventLogsFileProcessorTest {


    @Test
    @SuppressWarnings("unchecked")
    public void readSingleLogDataAndFail() {
        log.info("Test : readSingleLogDataAndFail");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("read_event_log.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(1, output.getUnprocessedEventLogs().size());
        assertEquals(0, output.getProcessedRecords().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void returnUnprocessedLogsData() {
        log.info("Test : returnUnprocessedLogsData");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("two_event_logs_of_different_events.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(0, output.getProcessedRecords().size());
        assertEquals(2, output.getUnprocessedEventLogs().size());
    }

    @Test
    public void shouldProcessWithCompleteLogData() {
        log.info("Test : shouldProcessWithCompleteLogData");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("two_event_logs_of_same_event.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(1, output.getProcessedRecords().size());
        assertEquals(0, output.getUnprocessedEventLogs().size());
    }

    @Test
    public void finishedEventLogOccurringBeforeStartedEventLog() {
        log.info("Test : finishedEventLogOccurringBeforeStartedEventLog");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("finished_event_before_started_event.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(1, output.getProcessedRecords().size());
        assertEquals(0, output.getUnprocessedEventLogs().size());
    }

    @Test
    public void partialCorrectPartialIncomplete() {
        log.info("Test : partialCorrectPartialIncomplete");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("partial_correct_partial_incomplete_event_logs.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(1, output.getProcessedRecords().size());
        assertEquals(1, output.getUnprocessedEventLogs().size());
    }

    @Test
    public void assignmentData() {
        log.info("Test : assignmentData");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("sample_event_log.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(3, output.getProcessedRecords().size());
        assertEquals(0, output.getUnprocessedEventLogs().size());
    }

    @Test
    public void findAlertTakingMoreThan4s() {
        log.info("Test : findAlertTakingMoreThan4s");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("4s_sample_event_log.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(2, output.getRecordsWithDurationMoreThanMs(new Long(4)).size());
    }

    @Test
    public void findAlertTakingMoreThan7s() {
        log.info("Test : findAlertTakingMoreThan7s");
        EventLogsFileProcessor eventLogsFileProcessor = new EventLogsFileProcessor(
                buildEventParameters("4s_sample_event_log.txt")
        );
        Output output = eventLogsFileProcessor.processEventLogsFile();
        assertEquals(1, output.getRecordsWithDurationMoreThanMs(new Long(7)).size());
    }

    private EventParameters buildEventParameters(String s) {
        return EventParameters.newInstance(
                ClassLoader.getSystemClassLoader().getResource(s).getPath(),
                1, 4
        );
    }
}
