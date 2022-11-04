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

/**
 * Expression is a string that may be resolved by a resolver
 */
public interface Expression {

    /**
     * Resolve immediate expressions within the expression, including perhaps this expression.
     *
     * @param resolver the resolver to resolve expression
     */
    void resolveImmediate(Resolver resolver);

    /**
     * Resolve expressions within this expressions, including perhaps this expression.
     *
     * @param resolver the resolver to resolve expressions
     * @return the resulting value
     */
    String resolve(Resolver resolver);
}