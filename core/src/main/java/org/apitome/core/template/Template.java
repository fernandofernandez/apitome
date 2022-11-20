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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * Template represents a JSON or YAML document that contains resolvable expressions.
 * These expressions may be resolved in two phases, which are implementation-dependent.
 * <ul>
 *     <li>an immediate expression is resolved in a first pass; example: ${expression}
 *     <li>a deferred expression is resolved in a second pass; example: #{expression}
 * </ul>
 *
 */
public class Template extends CompositeExpression {

    private final String name;

    public Template(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String resolve(Resolver resolver) {
        StringBuilder builder = new StringBuilder();
        getExpressions().forEach(expression -> builder.append(expression.resolve(resolver)));
        return builder.toString();
    }

    public static Template from(String name, InputStream inputStream) {
        Template template = new Template(name);
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             PushbackReader pbReader = new PushbackReader(streamReader, 2)) {
            processExpression(template, pbReader, false);
            return template;
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    private static boolean processExpression(CompositeExpression expression, PushbackReader pbReader, boolean inExpression) {
        Predicate<String> predicate;
        if (inExpression) {
            predicate = token -> token != null && !token.equals("}");
        } else {
            predicate = token -> token != null;
        }
        StringBuilder builder = new StringBuilder(132);
        String token = nextToken(builder, pbReader, inExpression);
        boolean eof;
        while (predicate.test(token)) {
            if ((token.startsWith("${") || token.startsWith("#{")) && token.endsWith("}")) {
                CompositeExpression compositeExpression = createCompositeExpression(token);
                expression.addExpression(compositeExpression);
            } else if ((token.startsWith("${") || token.startsWith("#{")) && !token.endsWith("}")) {
                CompositeExpression compositeExpression = createCompositeExpression(token);
                expression.addExpression(compositeExpression);
                eof = processExpression(compositeExpression, pbReader, true);
                if (eof) {
                    return true;
                }
            } else {
                ImmutableExpression immutableExpression = new ImmutableExpression(token);
                expression.addExpression(immutableExpression);
            }
            builder.setLength(0);
            token = nextToken(builder, pbReader, inExpression);
        }
        if (token == null) {
            return true;
        } else {
            ImmutableExpression immutableExpression = new ImmutableExpression(token);
            expression.addExpression(immutableExpression);
        }
        return false;
    }

    private static CompositeExpression createCompositeExpression(String constant) {
        CompositeExpression expression;
        if (constant.startsWith("${")) {
            expression = new ImmediateExpression();
        } else {
            expression = new DeferredExpression();
        }
        ImmutableExpression immutableExpression = new ImmutableExpression(constant);
        expression.addExpression(immutableExpression);
        return expression;
    }

    private static String nextToken(StringBuilder builder, PushbackReader reader, boolean inExpression) {
        try {
            int c = reader.read();
            while (c != -1) {
                char ch = (char) c;
                if (ch == '$' || ch == '#') {
                    c = reader.read();
                    if (c != -1) {
                        char delimiter = (char) c;
                        if (delimiter == '{') {
                            if (builder.length() == 0) {
                                builder.append(ch);
                                builder.append(delimiter);
                            } else {
                                reader.unread(delimiter);
                                reader.unread(ch);
                                break;
                            }
                        } else {
                            builder.append(ch);
                            builder.append(delimiter);
                        }
                    } else {
                        builder.append(ch);
                        break;
                    }
                } else if (ch == '}') {
                    if ((builder.length() > 2) &&
                            (builder.charAt(0) == '$' || builder.charAt(0) == '#') &&
                            builder.charAt(1) == '{') {
                        builder.append(ch);
                        break;
                    } else if (!inExpression) {
                        builder.append(ch);
                    } else {
                        if (builder.length() == 0) {
                            builder.append(ch);
                            break;
                        } else {
                            reader.unread(ch);
                            break;
                        }
                    }
                } else {
                    builder.append(ch);
                }
                c = reader.read();
            }
            if (c == -1) {
                if (builder.length() == 0) {
                    return null;
                }
                return builder.toString();
            } else {
                return builder.toString();
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }
}
