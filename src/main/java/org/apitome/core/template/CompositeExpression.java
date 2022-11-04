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

import org.apitome.core.expression.Resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CompositeExpression represents a expressions that contains embedded expressions
 */
public abstract class CompositeExpression implements Expression {

    private List<Expression> expressions;

    protected CompositeExpression() {
        this.expressions = new ArrayList<>();
    }

    @Override
    public void resolveImmediate(Resolver resolver) {
        List<Expression> expressions = getExpressions();
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            expression.resolveImmediate(resolver);
            if (expression instanceof ImmediateExpression) {
                ImmediateExpression immediateExpression = (ImmediateExpression) expression;
                AtomicBoolean onlyImmutable = new AtomicBoolean(true);
                immediateExpression.getExpressions()
                        .forEach(e -> { if (!(e instanceof ImmutableExpression)) onlyImmutable.set(false);});
                if (onlyImmutable.get()) {
                    String resolved = expression.resolve(resolver);
                    expressions.set(i, new ImmutableExpression(resolved));
                }
            }
        }
    }

    @Override
    public String resolve(Resolver resolver) {
        StringBuilder builder = new StringBuilder();
        getExpressions().forEach(expression -> builder.append(expression.resolve(resolver)));
        String expression = builder.toString();
        return resolver.processExpression(expression);
    }

    protected void addExpression(Expression expression) {
        this.expressions.add(expression);
    }

    protected List<Expression> getExpressions() {
        return expressions;
    }
}
