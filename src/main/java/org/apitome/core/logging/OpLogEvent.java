package org.apitome.core.logging;

public enum OpLogEvent implements LogEvent {

    ACTION_COMPLETED,
    ACTION_EXCEPTION,
    ACTION_TIMEOUT,
    SERVICE_EXCEPTION;

    @Override
    public String getName() {
        return name();
    }
}
