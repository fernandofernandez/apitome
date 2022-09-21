package org.apitome.core.logging;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;

public interface LoggerAware {

    Logger getLogger();

    default void logInfo(String message, LogFields logFields) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            StructuredArgument argument = StructuredArguments.entries(logFields.getLogFields());
            logger.info(message + " {}", argument);
        }
    }

    default void logInfo(LogFields logFields) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            StructuredArgument argument = StructuredArguments.entries(logFields.getLogFields());
            logger.info("{}", argument);
        }
    }

    default void logError(LogFields logFields) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            StructuredArgument argument = StructuredArguments.entries(logFields.getLogFields());
            logger.error("{}", argument);
        }
    }

    default void logError(LogFields logFields, Exception exception) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            StructuredArgument argument = StructuredArguments.entries(logFields.getLogFields());
            logger.error("{}", argument, exception);
        }
    }
}
