/*
 * Copyright (c) 2022. Fernando Fernandez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
