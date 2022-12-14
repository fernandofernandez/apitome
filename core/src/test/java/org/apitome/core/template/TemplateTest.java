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

import org.apitome.core.expression.SimpleResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateTest {

    private PathMatchingResourcePatternResolver resourceResolver;

    private Properties properties;

    private SimpleResolver simpleResolver;

    @BeforeEach
    public void setup() {
        this.resourceResolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        this.properties = new Properties();
        this.simpleResolver = new SimpleResolver(properties);
    }

    @Test
    public void testSimpleTemplate() throws IOException {
        Resource resource = resourceResolver.getResource("template/simple-template.json");
        Template template = Template.from("simple", resource.getInputStream());
        assertEquals("simple", template.getName());
        assertEquals(5, template.getExpressions().size());
        assertTrue(template.getExpressions().get(0) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(1) instanceof ImmediateExpression);
        assertTrue(template.getExpressions().get(2) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(3) instanceof DeferredExpression);
        assertTrue(template.getExpressions().get(4) instanceof ImmutableExpression);
        properties.put("propertyOne", "test");
        properties.put("propertyTwo", 4);
        properties.put("propertyThree", 3);
        template.resolveImmediate(simpleResolver);
        assertEquals(5, template.getExpressions().size());
        assertTrue(template.getExpressions().get(0) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(1) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(2) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(3) instanceof DeferredExpression);
        assertTrue(template.getExpressions().get(4) instanceof ImmutableExpression);
        String result = template.resolve(simpleResolver);
        assertTrue(result.contains("\"type\": \"test\","));
        assertTrue(result.contains("\"strValue\": \"4\""));
    }

    @Test
    public void testEmbeddedTemplate() throws IOException {
        Resource resource = resourceResolver.getResource("template/embedded-template.json");
        Template template = Template.from("embedded", resource.getInputStream());
        assertEquals("embedded", template.getName());
        assertEquals(5, template.getExpressions().size());
        assertTrue(template.getExpressions().get(0) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(1) instanceof ImmediateExpression);
        assertTrue(template.getExpressions().get(2) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(3) instanceof DeferredExpression);
        assertTrue(template.getExpressions().get(4) instanceof ImmutableExpression);
        properties.put("propertyOne", "property");
        properties.put("propertyTwo", "five");
        properties.put("property.five", "epitome");
        properties.put("propertyThree", "property");
        properties.put("propertyFour", "Five");
        properties.put("propertyFive", "apitome");
        template.resolveImmediate(simpleResolver);
        assertEquals(5, template.getExpressions().size());
        assertTrue(template.getExpressions().get(0) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(1) instanceof ImmutableExpression);
        assertEquals("epitome", template.getExpressions().get(1).resolve(null));
        assertTrue(template.getExpressions().get(2) instanceof ImmutableExpression);
        assertTrue(template.getExpressions().get(3) instanceof DeferredExpression);
        DeferredExpression deferredExpression = (DeferredExpression) template.getExpressions().get(3);
        assertEquals(4, deferredExpression.getExpressions().size());
        assertTrue(deferredExpression.getExpressions().get(0) instanceof ImmutableExpression);
        assertEquals("#{", deferredExpression.getExpressions().get(0).resolve(null));
        assertTrue(deferredExpression.getExpressions().get(1) instanceof ImmutableExpression);
        assertTrue(deferredExpression.getExpressions().get(2) instanceof ImmutableExpression);
        assertTrue(deferredExpression.getExpressions().get(3) instanceof ImmutableExpression);
        assertEquals("}", deferredExpression.getExpressions().get(3).resolve(null));
        assertTrue(template.getExpressions().get(4) instanceof ImmutableExpression);
        String result = template.resolve(simpleResolver);
        assertTrue(result.contains("\"type\": \"epitome\","));
        assertTrue(result.contains("\"strValue\": \"apitome\""));
    }
}
