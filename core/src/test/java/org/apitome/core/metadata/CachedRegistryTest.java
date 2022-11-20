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

package org.apitome.core.metadata;

import org.apitome.core.expression.Resolver;
import org.apitome.core.expression.SimpleResolver;
import org.apitome.core.model.TestOperationDescription;
import org.apitome.core.model._TestOperationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CachedRegistryTest {

    private Properties properties;

    private CachedRegistry<TestOperationDescription, _TestOperationDescription, OperationDescriptionTransformer> registry;

    @BeforeEach
    public void setup() {
        this.properties = new Properties();
        this.registry = new TestCachedRegistry(new SimpleResolver(properties));
    }

    @Test
    public void testGetDescription() {
        registry.processResources("test/operation/*.*");
        TestOperationDescription result = registry.getDescription("testoperationA");
        assertNotNull(result);
        result = registry.getDescription("testoperationA");
        assertNotNull(result);
    }

    @Test
    public void testGetDescriptionNotFound() {
        registry.processResources("test/operation/*.*");
        TestOperationDescription result = registry.getDescription("notfound");
        assertNull(result);
    }

    public class TestCachedRegistry extends CachedRegistry<TestOperationDescription, _TestOperationDescription, OperationDescriptionTransformer> {


        public TestCachedRegistry(Resolver resolver) {
            super(_TestOperationDescription.class, new OperationDescriptionTransformer(), resolver);
        }
    }
}
