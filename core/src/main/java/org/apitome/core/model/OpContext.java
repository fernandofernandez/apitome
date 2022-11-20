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

package org.apitome.core.model;

import org.apitome.core.logging.LogFields;

import java.util.HashMap;
import java.util.Map;

public class OpContext implements Context {

    private final LogFields commonLogFields;

    private final Map<TypeKey<?>, Object> valueMap;

    public OpContext() {
        this.valueMap = new HashMap<>();
        this.commonLogFields = LogFields.builder(null).build();
    }

    @Override
    public <T> void put(TypeKey<T> key, T value) {
        valueMap.put(key, value);
    }

    @Override
    public <T> T get(TypeKey<T> key) {
        Object valueObj = valueMap.get(key);
        return key.getType().cast(valueObj);
    }

    @Override
    public LogFields getCommonLogFields() {
        return commonLogFields;
    }
}
