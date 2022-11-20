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

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A Spliterator implementation that enables template processing as a stream and with
 * concurrency
 */
public class InputSpliterator implements Spliterator<Template> {

    protected static int MINIMUM_PARTITION_SIZE = 100;

    private int maxConcurrency;

    private int begin;

    private final int end;

    private final List<TemplateInput> inputs;

    public InputSpliterator(List<TemplateInput> inputs, int maxConcurrency) {
        this.inputs = inputs;
        this.maxConcurrency = maxConcurrency;
        this.begin = 0;
        this.end = inputs.size();
    }

    private InputSpliterator(List<TemplateInput> inputs, int begin, int end) {
        this.inputs = inputs;
        this.maxConcurrency = 1; // split instance does not split up
        this.begin = begin;
        this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Template> action) {
        if (begin >= end) {
            return false;
        }
        TemplateInput input = inputs.get(begin);
        Template template = Template.from(input.getTemplateName(), input.getInputStream());
        action.accept(template);
        begin++;
        return true;
    }

    @Override
    public Spliterator<Template> trySplit() {
        if (maxConcurrency <= 1) {
            return null;
        }
        int partitionSize = (end - begin) / maxConcurrency;
        if (partitionSize < MINIMUM_PARTITION_SIZE) {
            return null;
        }
        this.maxConcurrency -= 1;
        int newEnd = begin + partitionSize;
        this.begin = newEnd;
        return new InputSpliterator(inputs, begin, newEnd);
    }

    @Override
    public long estimateSize() {
        return (end - begin);
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED;
    }
}
