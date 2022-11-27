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

import org.apitome.core.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputSpliteratorTest {

    private static String TEST_JSON = "{ \"field\": \"value\" }";

    private InputSpliterator spliterator;

    private List<TemplateInput> list;

    @Test
    public void testSingleThread() {
        this.list = createInputList(100);
        this.spliterator = new InputSpliterator(list, 1);
        Stream<Template> testStream = StreamSupport.stream(spliterator, false);
        List<Template> testMap = testStream.collect(Collectors.toList());
        assertEquals(100, testMap.size());
    }

    @Test
    public void testMultipleThread() {
        this.list = createInputList(301);
        this.spliterator = new InputSpliterator(list, 2);
        Stream<Template> testStream = StreamSupport.stream(spliterator, true);
        List<Template> testMap = testStream.collect(Collectors.toList());
        assertEquals(301, testMap.size());
    }

    private List<TemplateInput> createInputList(int size) {
        List<TemplateInput> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            String name = "input" + i + ".json";
            InputStream inputStream = new ByteArrayInputStream(TEST_JSON.getBytes());
            TemplateInput input = new TemplateInput(name, inputStream);
            list.add(input);
        }
        return list;
    }
}
