# DevOps KPI Tool  
Purpose: Identify if there is any process which is taking longer than expected and raise an alert.

Use Cases:
- CICD measurement - build time, upload time, test time, deployment time, start/stop/report time, etc
- Service API call turnaround time measurement


 

####Prerequisites
- Gradle 6.7
- Java 1.8



##(Windows)
#### How to run  
1. Download the code into your desktop machine to 'C:/' directory
2. open command prompt and navigate inside the downloaded project folder
3. Execute `gradlew run` ; it will display the expected command to execute
   ```
      gradlew run --args='<LOG_FILE_PATH> <THREAD_COUNT> <DURATION_THRESHOLD>'
   
                 LOG_FILE_PATH      :    (Mandatory)                             | Path of the file which needs to be checked.
                 THREAD_COUNT       :    (Optional | Default : 1)                | Number of thread worker to process record concurrently
                 DURATION_THRESHOLD :    (Optional | Default : 4 milli secs)     | Minimum threshold value in milli seconds. so that process can raise alert if time span exceeds
   
   
      example:
          gradlew run --args='.\src\test\resources\sample_event_log.txt'
          gradlew run --args='.\src\test\resources\sample_event_log.txt 1 4'
   
   ```

#### Run pre written test
1. Execute
   ```
    gradlew test
     
   ```

2. Report can be found in the directory 'build\reports\tests\test\index.html'

3. Sample files can be found in 'src\test\resources'


### About the output

### 1. HSQL Database 
**Database Details**:  DB Name: `devops.kpi`  |   Table Name : `PROCESSED_ENTITY`  |   Logs : 'devops.kpi.script' / 'devops.kpi.log'

**Utility Java Class** to manage database operations : `ProcessDaoImpl` and `HsqlDbManager`

1. Table is created on start up, and only if it doesn't exist. 
   - Table is not dropped/deleted after the process ends. If required then it can be dropped by method `ProcessDaoImpl.dropProcessTable()`)
2. All successfully processed events are inserted into the table PROCESSED_ENTITY.
      - SQL can be seen in loggers when log level is debug 
3. If new event with same id is found (at later stage of log scan or after re-running the process against another log file), then that event id record is updated with new duration/alert/host information.
    -if not required then it can be commented out 


### 2. Java Bean 
1. Output.java holds successfully processed Events and Unable to process Log Events due to missing data.
2. Method `getRecordsWithDurationMoreThanMs(Long milliSecDuration)` can be used with in java to identify events that took longer than passed duration.
    - it is out of the box and irrespective of the DURATION_THRESHOLD (default 4ms)


###3. Informative Loggers
Log level change be changed in file `logback.xml` (current level is INFO)
```
1. Incomplete / Unprocessed events 
    -It will hold event's whose either STARTED or FINISHED log is not present in the log file
2. Processed events
    -All successfully procesed events
3.  Events took more than threshold
    -All successfully processed events whose duration is longer than 4ms ( 4ms is default value for DURATION_THRESHOLD, can be configured as part of arguments). 
```

Sample info loggers 
```
---------------------------------------------------------------
 Incomplete / Unprocessed events :	 {} |
 Processed events :			 [ProcessedEntity(processId=scsmbstgra, duration=5, host=12345, type=APPLICATION_LOG), ProcessedEntity(processId=scsmbstgrc, duration=8, host=null, type=null), ProcessedEntity(processId=scsmbstgrb, duration=3, host=null, type=null)] |
 Events took more than threshold:	 [ProcessedEntity(processId=scsmbstgra, duration=5, host=12345, type=APPLICATION_LOG), ProcessedEntity(processId=scsmbstgrc, duration=8, host=null, type=null)] |
 -------------------------------------------------------------


---------------------------------------------------------------
 Incomplete / Unprocessed events :	 {scsmbstgrb=EventLog(id=scsmbstgrb, state=STARTED, timestamp=1491377495213, type=null, host=null)} |
 Processed events :			 [ProcessedEntity(processId=scsmbstgra, duration=10, host=12345, type=APPLICATION_LOG)] |
 Events took more than threshold:	 [ProcessedEntity(processId=scsmbstgra, duration=10, host=12345, type=APPLICATION_LOG)] |
 -------------------------------------------------------------


findAlertTakingMoreThan7s
---------------------------------------------------------------
 Incomplete / Unprocessed events :	 {} |
 Processed events :			 [ProcessedEntity(processId=scsmbstgra, duration=5, host=12345, type=APPLICATION_LOG), ProcessedEntity(processId=scsmbstgrc, duration=8, host=null, type=null), ProcessedEntity(processId=scsmbstgrb, duration=3, host=null, type=null)] |
 Events took more than threshold:	 [ProcessedEntity(processId=scsmbstgra, duration=5, host=12345, type=APPLICATION_LOG), ProcessedEntity(processId=scsmbstgrc, duration=8, host=null, type=null)] |
 -------------------------------------------------------------


```



