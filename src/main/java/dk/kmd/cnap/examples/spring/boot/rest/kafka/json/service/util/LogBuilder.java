package dk.kmd.cnap.examples.spring.boot.rest.kafka.json.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;
import static org.slf4j.event.Level.INFO;

public class LogBuilder {
    private String loggerName = getClass().getName();
    private Level level = INFO;
    private String message = null;
    private Throwable throwable = null;
    private Map<String, Object> parameterByKey = new LinkedHashMap();

    public LogBuilder loggerName(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    public LogBuilder loggerName(Class loggerClass) {
        this.loggerName = loggerClass.getName();
        return this;
    }

    public LogBuilder level(Level level) {
        this.level = level;
        return this;
    }

    public LogBuilder message(String message) {
        this.message = message;
        return this;
    }

    public LogBuilder throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public LogBuilder parameter(String key, Object value) {
        this.parameterByKey.put(key, value);
        return this;
    }

    public void reset() {
        this.loggerName = null;
        this.level = null;
        this.message = null;
        this.throwable = null;
        this.parameterByKey = new LinkedHashMap();
    }

    Map parameterMap() {
        Map map = new LinkedHashMap();
        map.put("logger-name", loggerName);
        if (parameterByKey!=null) {
            map.putAll(parameterByKey);
        }
        return map;
    }

    public Logger build() {
        return build(logger());
    }

    public Logger logger() {
        Logger logger = LoggerFactory.getLogger(loggerName);
        return logger;
    }

    public Logger build(Logger logger) {
        if (logger==null) {
            throwLogBuilderConfigurationException("Missing logger");
        }
        if (loggerName==null) {
            throwLogBuilderConfigurationException("Missing loggerName");
        }
        if (level==null) {
            throwLogBuilderConfigurationException("Missing level");
        }
        if (message==null) {
            throwLogBuilderConfigurationException("Missing message");
        }

        Map parameterMap = parameterMap();
        switch (level) {
            case DEBUG:
                logger.debug(message, entries(parameterMap), throwable);
                break;
            case ERROR:
                logger.error(message, entries(parameterMap), throwable);
                break;
            case INFO:
                logger.info(message, entries(parameterMap), throwable);
                break;
            case TRACE:
                logger.trace(message, entries(parameterMap), throwable);
                break;
            case WARN:
                logger.warn(message, entries(parameterMap), throwable);
                break;
        }
        reset();
        return logger;
    }

    public static LogBuilder logBuilder() {
        return new LogBuilder();
    }

    void throwLogBuilderConfigurationException(String message) {
        throw new LogBuilderConfigurationException(message, level, loggerName);
    }

    public static class LogBuilderConfigurationException extends RuntimeException {
        private Level level;
        private String loggerName;

        public LogBuilderConfigurationException(String message, Level level, String loggerName) {
            super(message);
            this.level = level;
            this.loggerName = loggerName;
        }

        public Level getLevel() {
            return level;
        }

        public String getLoggerName() {
            return loggerName;
        }
    }
}
