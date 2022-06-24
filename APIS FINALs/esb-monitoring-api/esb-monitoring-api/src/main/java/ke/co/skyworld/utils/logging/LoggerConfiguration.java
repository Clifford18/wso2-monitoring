package ke.co.skyworld.utils.logging;

import ke.co.skyworld.utils.Constants;
import org.apache.log4j.*;

import java.util.Properties;

/**
 * flexicore (ke.co.skyworld.utilities.logging)
 * Created by: elon
 * On: 18 Jul, 2019 18/07/19 13:45
 **/
public class LoggerConfiguration {

    public static void initialize(){
        //TODO: Implement in production (get from sw.sky)
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();

        Properties properties = new Properties();
        //properties.put("log4j.rootLogger", "DEBUG, stdout, file");
        properties.put("log4j.rootLogger", "INFO, stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.stdout.Target", "System.out");
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.stdout.layout.ConversionPattern", "%5p [thread-%t] - %d{yyyy-MM-dd HH:mm:ss.SSS}; - %c{1}%m%n");
//        properties.put("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
//        properties.put("log4j.appender.file.File", "tmp/logs/sw.log");
//        properties.put("log4j.appender.file.MaxFileSize", "10000KB");
//        properties.put("log4j.appender.file.MaxBackupIndex", "10");
//        properties.put("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
//        properties.put("log4j.appender.file.layout.ConversionPattern", "%5p %t - %d{yyyy-MM-dd HH:mm:ss.SSS}; - %c [thread-%t] - (%F:%L) - %m%n");

        PropertyConfigurator.configure(properties);
    }
}