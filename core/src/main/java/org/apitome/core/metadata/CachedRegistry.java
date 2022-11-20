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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * A registry implementation that caches resources from the application as templates and processes
 * them into descriptions when requested.
 *
 * @param <D> the description type
 * @param <R> the raw description type
 * @param <T> the transformer type
 */
public class CachedRegistry <D, R extends Transformable<D, T>, T extends Transformer> extends AbstractRegistry<D, R, T> {

    protected final ConcurrentHashMap<String, Template> templateMap;

    public CachedRegistry(Class<R> rawDescriptionClass, T transformer, Resolver resolver) {
        super(rawDescriptionClass, transformer, resolver);
        this.templateMap = new ConcurrentHashMap<>();
    }

    @Override
    public D getDescription(String descriptionName) {
        D description = descriptionMap.get(descriptionName.toLowerCase());
        if (description != null) {
            return description;
        }
        Template template = templateMap.get(descriptionName.toLowerCase());
        if (template != null) {
            description = processTemplate(template);
            descriptionMap.put(descriptionName.toLowerCase(), description);
            return description;
        }
        return null;
    }

    public void processResources(String locationPattern) {
        Stream<Template> templates = getResources(locationPattern);
        Map<String, Template> descriptions = templates
                .collect(toMap(template -> getTemplateName(template.getName()).toLowerCase(), template -> template));
        templateMap.putAll(descriptions);
    }

    public void processResource(String location) {
        Template template = getResource(location);
        templateMap.putIfAbsent(getTemplateName(template.getName()), template);
    }
}
