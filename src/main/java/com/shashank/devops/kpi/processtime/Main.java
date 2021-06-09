package com.shashank.devops.kpi.processtime;

import com.shashank.devops.kpi.common.exception.FilePathParameterMissingException;
import com.shashank.devops.kpi.common.exception.ProcessingEventLogsFileException;
import com.shashank.devops.kpi.processtime.models.EventParameters;
import com.shashank.devops.kpi.processtime.models.Output;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main {

    public static void main(String... vars) {
        System.out.println(getInfo1());
        EventParameters params = null;

        try {
            params = new EventParametersValidator(vars).getEventParameters();
        } catch(FilePathParameterMissingException e){
            System.out.println(getHelp());
            exit(true);
        }

        EventLogsFileProcessor processor = new EventLogsFileProcessor(params);
        try {
            Output output = processor.processEventLogsFile();
            //output.export();//todo export data in csv file or to generate report
        } catch(ProcessingEventLogsFileException fe){
            log.error("\n\n\n\t There was an error while processing file. Cause -  {} \n\n", fe.getMessage());
            exit(true);
        }
        log.info("Process end.");
        exit(false);
    }


    private static void exit(boolean error){
        System.out.println(getExitText(error));
        Runtime.getRuntime().exit(error?1:0);
    }




    private static String getHelp(){
        String help = "\n\n \t Invalid Arguments!!! \n -------------------------------------------------------------------------------------------------------------------------------------------------------\n " +
                "\n HELP: \n  gradlew run --args='<LOG_FILE_PATH> <THREAD_COUNT> <DURATION_THRESHOLD>' " +
                "\n\n \t\t LOG_FILE_PATH : \t (Mandatory) \t\t\t\t | Path of the file which needs to be checked. " +
                "\n \t\t THREAD_COUNT : \t\t (Optional | Default : 1) \t\t | Number of thread worker to process record concurrently  " +
                "\n \t\t DURATION_THRESHOLD : \t (Optional | Default : 4 milli secs) \t | Minimum threshold value in milli seconds. so that process can raise alert if time span exceeds  " +

                "\n\n \t sample calls:  " +
                "\n\t  gradlew run --args='.\\src\\test\\resources\\sample_event_log.txt'" +
                "\n\t  gradlew run --args='.\\src\\test\\resources\\sample_event_log.txt 1 4'" +
                "\n -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

        return help;
    }

    private static String getInfo1(){
        String txt = "-------------------------------------------------------------------------------------------------- \n \n \t \t " +
                " Shashank DevOps KPI tool - ProcessTime checker " +
                "\n \n-------------------------------------------------------------------------------------------------- \n ";

        return txt;
    }

    private static String getExitText( boolean error){
        String txt = "-------------------------------------------------------------------------------------------------- \n \n \t \t " +
                " Shashank DevOps KPI tool - Process ended with" + (error?"":" no") +" error." +
                "\n \n-------------------------------------------------------------------------------------------------- \n ";

        return txt;
    }
}
