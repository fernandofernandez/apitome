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

import com.sun.el.ExpressionFactoryImpl;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

public class TemplateContext {

    private final ExpressionContext expressionContext;
    private final ExpressionFactory expressionFactory;

    public TemplateContext(Map<Object, Object> propertyMap) {
        this.expressionFactory = new ExpressionFactoryImpl();
        PropertyResolver propertyResolver = new PropertyResolver(propertyMap);
        this.expressionContext = new ExpressionContext(propertyResolver);
    }

    public String evaluateExpression(String expression) {
        ValueExpression valueExpression = expressionFactory.createValueExpression(expressionContext, expression, Object.class);
        Object value = valueExpression.getValue(expressionContext);
        if (value == null) {
            return "null";
        }
        return value.toString();
    }
}
