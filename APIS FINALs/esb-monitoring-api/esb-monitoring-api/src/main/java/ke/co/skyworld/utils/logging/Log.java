package ke.co.skyworld.utils.logging;

import ke.co.skyworld.domain.enums.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * flexicore (ke.co.skyworld.utilities.logging)
 * Created by: elon
 * On: 18 Jul, 2019 18/07/19 13:33
 **/

public class Log {

    private static void log(Logger logger, LoggingLevel loggingLevel, String message){
        switch (loggingLevel){
            case DEBUG:
                logger.debug(message);
                break;

            case INFO:
                logger.info(message);
                break;

            case WARNING:
                logger.warn(message);
                break;

            case ERROR:
                logger.error(message);
                break;
        }
    }

    public static void debug(Class clazz, String methodName, Object message){
        Logger logger = LoggerFactory.getLogger(clazz);
        log(logger, LoggingLevel.DEBUG, "."+methodName+"() - "+message);
    }

    public static void info(Class clazz, String methodName, Object message){
        Logger logger = LoggerFactory.getLogger(clazz);
        log(logger, LoggingLevel.INFO, "."+methodName+"() - "+message);
    }

    public static void warning(Class clazz, String methodName, Object message){
        Logger logger = LoggerFactory.getLogger(clazz);
        log(logger, LoggingLevel.WARNING, "."+methodName+"() - "+message);
    }

    public static void error(Class clazz, String methodName, Object message){
        Logger logger = LoggerFactory.getLogger(clazz);
        log(logger, LoggingLevel.ERROR, "."+methodName+"() - "+message);
    }
}