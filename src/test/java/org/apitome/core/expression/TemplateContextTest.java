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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateContextTest {

    private Map<Object, Object> propertyMap;
    private TemplateContext context;

    @BeforeEach
    public void setup() {
        this.propertyMap = new HashMap<>();
        this.context = new TemplateContext(propertyMap);
    }

    @Test
    public void testSimpleVariableReplacement() {
        propertyMap.put("weAreTesting", "yeah, yeah");
        String result = context.evaluateExpression("${weAreTesting}");
        assertEquals("yeah, yeah", result);
    }

    @Test
    public void testSimpleCamelVariableReplacement() {
        propertyMap.put("we_are_testing", "yeah, yeah");
        String result = context.evaluateExpression("${we_are_testing}");
        assertEquals("yeah, yeah", result);
    }

    @Test
    public void testSimpleExpression() {
        String result = context.evaluateExpression("${1 + 2}");
        assertEquals("3", result);
    }
}
