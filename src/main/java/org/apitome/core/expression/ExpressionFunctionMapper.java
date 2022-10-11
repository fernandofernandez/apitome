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

package org.apitome.core.expression;

import org.apitome.core.error.ConfigurationException;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class ExpressionFunctionMapper extends FunctionMapper {

    private static ZoneId zoneId = ZoneId.systemDefault();

    private static Instant now = Instant.now();

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StringBuilder builder;
    private final Map<String, Method> functionMap;

    public ExpressionFunctionMapper() {
        this.functionMap = new HashMap<>();
        this.builder = new StringBuilder(32);
        try {
            Method method = getClass().getMethod("today", new Class[] {});
            functionMap.put("today", method);
            method = getClass().getMethod("todayPlusDays", new Class[] {Long.class});
            functionMap.put("todayPlusDays", method);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override
    public Method resolveFunction(String prefix, String localName) {
        builder.setLength(0);
        if (prefix != null) {
            builder.append(prefix);
            builder.append(':');
        }
        builder.append(localName);
        Method method = functionMap.get(builder.toString());
        if (method == null) {
            throw new ConfigurationException(new RuntimeException(""));
        }
        return method;
    }

    public static String today() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, zoneId);
        return dateFormatter.format(localDateTime);
    }

    public static String todayPlusDays(Long days) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now.plus(days, DAYS), zoneId);
        return dateFormatter.format(localDateTime);
    }
}
