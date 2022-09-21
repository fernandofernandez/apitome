package org.apitome.core.logging;

import java.util.HashMap;
import java.util.Map;

import static org.apitome.core.logging.OpLogKey.EVENT;

public class LogFields {

    private final Map<LogKey, Object> logFields;

    protected LogFields(Map<LogKey, Object> logFields) {
        this.logFields = logFields;
    }

    public Map<LogKey, Object> getLogFields() {
        return logFields;
    }

    public static LogEventBuilder builder(LogEvent event) {
        return new LogEventBuilder(event);
    }

    public static class LogEventBuilder {

        private final Map<LogKey, Object> keyValueMap;

        public LogEventBuilder(LogEvent event) {
            this.keyValueMap = new HashMap<>();
            keyValueMap.put(EVENT, event);
        }
        public LogEventBuilder addKeyValue(LogKey key, Object value) {
            keyValueMap.put(key, value);
            return this;
        }
        public LogFields build() {
            return new LogFields(keyValueMap);
        }
    }
}
