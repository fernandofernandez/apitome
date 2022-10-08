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

package org.apitome.core.template;

import org.apitome.core.error.ConfigurationException;
import org.apitome.core.expression.Resolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Template {

    private List<Expression> expressions;

    private final String name;

    public Template(String name) {
        this.name = name;
        this.expressions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    protected void appendString(String string) {
        expressions.add(new ImmutableExpression(string));
    }

    protected void appendExpression(String expression) {
        expressions.add(new ComplexExpression(expression));
    }

    public String toString(Resolver resolver) {
        StringBuilder builder = new StringBuilder();
        expressions.forEach(expression -> builder.append(expression.accept(resolver)));
        return builder.toString();
    }

    public static Template from(String name, InputStream inputStream) {
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)) {
            Template builder = new Template(name);
            reader.lines().forEach(l -> parseExpressions(l, builder));
            return builder;
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    private static void parseExpressions(String line, Template template) {
        int start = 0;
        int end;
        int ix = line.indexOf("${");
        while (ix > -1) {
            end = line.indexOf('}', ix+2);
            if (end > -1) {
                template.appendString(line.substring(start, ix));
                template.appendExpression(line.substring(ix+2, end));
                start = end + 1;
            } else {
                template.appendString(line.substring(start, ix+2));
                start = ix + 2;
            }
            ix = line.indexOf("${");
        }
        template.appendString(line.substring(start) + "\n");
    }
}
