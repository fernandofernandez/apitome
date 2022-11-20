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
