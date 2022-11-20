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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apitome.core.expression.Resolver;
import org.apitome.core.template.Template;

import java.util.concurrent.ConcurrentHashMap;

import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES;

/**
 * An abstract registry implementation that supports processing raw descriptions as JSON and YAML files
 *
 * @param <D> the description type
 * @param <R> the raw description type
 * @param <T> the transformer type
 */
public abstract class AbstractRegistry<D, R extends Transformable<D, T>, T extends Transformer> implements Registry<D, R, T> {

    private static final String NAME_EXT_SEPARATOR = "\\.(?=[^\\.]+$)";

    private static final String JSON_EXT = "json";

    private static final String YAML_EXT = "yaml";

    private static final String YML_EXT = "yml";

    protected final ObjectMapper yamlMapper;

    protected final ObjectMapper jsonMapper;

    protected final ConcurrentHashMap<String, D> descriptionMap;

    private final Resolver resolver;

    private final T transformer;

    private final Class<R> rawDescriptionClass;

    public AbstractRegistry(Class<R> rawDescriptionClass, T transformer, Resolver resolver) {
        this.rawDescriptionClass = rawDescriptionClass;
        this.transformer = transformer;
        this.resolver = resolver;
        this.descriptionMap = new ConcurrentHashMap<>();
        this.jsonMapper = JsonMapper.builder()
                .configure(ACCEPT_CASE_INSENSITIVE_VALUES, true)
                .build();
        this.yamlMapper = JsonMapper.builder(new YAMLFactory())
                .configure(ACCEPT_CASE_INSENSITIVE_VALUES, true)
                .build();
    }

    @Override
    public D getDescription(String descriptionName) {
        if (descriptionName != null) {
            descriptionName = descriptionName.toLowerCase();
        }
        return descriptionMap.get(descriptionName);
    }

    @Override
    public Class<R> getRawDescriptionClass() {
        return rawDescriptionClass;
    }

    @Override
    public T getTransformer() {
        return transformer;
    }

    @Override
    public Resolver getResolver() {
        return resolver;
    }

    protected D processTemplate(Template template) {
        String templateExt = getTemplateExt(template.getName());
        R rawDescription;
        if (isJson(templateExt)) {
            rawDescription = getRawDescriptionAsJson(template);
        } else if (isYaml(templateExt)) {
            rawDescription = getRawDescriptionAsYaml(template);
        } else {
            // if ext not recognized, attempt to parse it as JSON
            rawDescription = getRawDescriptionAsJson(template);
        }
        return transformDescription(rawDescription);
    }

    /**
     * Get a raw description from a template, processing its contents as JSON
     *
     * @param template a template
     * @return a raw description
     */
    protected R getRawDescriptionAsJson(Template template) {
        return getRawDescription(template, t -> jsonMapper.readValue(t.resolve(getResolver()), getRawDescriptionClass()));
    }

    /**
     * Get a raw description from a template, processing its contents as YAML
     *
     * @param template a template
     * @return a raw description
     */
    protected R getRawDescriptionAsYaml(Template template) {
        return getRawDescription(template, t -> yamlMapper.readValue(t.resolve(getResolver()), getRawDescriptionClass()));
    }

    /**
     * Get the template name
     * @param templateName
     * @return
     */
    protected String getTemplateName(String templateName) {
        String[] namePart = templateName.split(NAME_EXT_SEPARATOR);
        return namePart[0];
    }

    protected String getTemplateExt(String templateName) {
        String[] namePart = templateName.split(NAME_EXT_SEPARATOR);
        if (namePart.length == 1) {
            return null;
        }
        return namePart[1];
    }

    protected boolean isJson(String ext) {
        return JSON_EXT.equalsIgnoreCase(ext);
    }

    protected boolean isYaml(String ext) {
        return YAML_EXT.equalsIgnoreCase(ext) || YML_EXT.equalsIgnoreCase(ext);
    }
}
