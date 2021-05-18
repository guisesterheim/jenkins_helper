package com.logistics.jenkinsHelper.util;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

public class LoggerUtil {

    public static boolean enableConsoleLogs;
    private static Logger logger;

    public static void setEnableConsoleLogs(String param){
        logger = Logger.getRootLogger();
        logger.addAppender(new NullAppender());

        if(param != null && !param.trim().equals("") &&
                (param.equals("true") || param.equals("false"))){
            enableConsoleLogs = Boolean.parseBoolean(param);
        }
    }

    // TODO: send logs to ELK
    public static void logInfo(String message){
        if(enableConsoleLogs)
            logger.info("INFO: " + message);
    }

    public static void logError(String message){
        if(enableConsoleLogs)
            logger.error("ERROR: "+message);
    }

    public static void logWarning(String message){
        if(enableConsoleLogs)
            logger.warn("WARNING: "+message);
    }

    public static void logDebug(String message){
        if(enableConsoleLogs)
            logger.debug("DEBUG: "+message);
    }

}
