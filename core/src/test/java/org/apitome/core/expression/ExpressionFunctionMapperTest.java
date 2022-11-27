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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionFunctionMapperTest {

    private ExpressionFunctionMapper mapper;

    @BeforeEach
    public void setup() {
        this.mapper = new ExpressionFunctionMapper();
    }

    @Test
    public void testResolveFunctionSuccess() {
        Method result = mapper.resolveFunction(null, "today");
        assertNotNull(result);
    }

    @Test
    public void testResolveFunctionNotFound() {
        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> mapper.resolveFunction("nonexisting", "today"));
    }

    @Test
    public void testFunctionToday() {
        assertNotNull(ExpressionFunctionMapper.today());
    }

    @Test
    public void testFunctionTodayPlusDays() {
        assertNotNull(ExpressionFunctionMapper.todayPlusDays(2L));
    }
}
