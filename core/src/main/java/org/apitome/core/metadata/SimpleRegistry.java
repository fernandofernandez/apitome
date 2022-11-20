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
import org.apitome.core.template.Template;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * A simple registry implementation that processes resources from the application, using
 * the resource name as index.
 *
 * @param <D> the description type
 * @param <R> the raw description type
 * @param <T> the transformer type
 */
public class SimpleRegistry<D, R extends Transformable<D, T>, T extends Transformer> extends AbstractRegistry<D, R, T> {

    public SimpleRegistry(Class<R> rawDescriptionClass, T transformer, Resolver resolver) {
        super(rawDescriptionClass, transformer, resolver);
    }

    public void processResources(String locationPattern) {
        Stream<Template> templates = getResources(locationPattern);
        Map<String, D> descriptions = templates
                .collect(toMap(template -> getTemplateName(template.getName()).toLowerCase(), template -> processTemplate(template)));
        descriptionMap.putAll(descriptions);
    }

    public void processResource(String location) {
        Template template = getResource(location);
        D description = processTemplate(template);
        descriptionMap.putIfAbsent(getTemplateName(template.getName()), description);
    }
}
