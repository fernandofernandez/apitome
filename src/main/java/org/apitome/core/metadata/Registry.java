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

import org.apitome.core.error.ConfigurationException;
import org.apitome.core.expression.Resolver;
import org.apitome.core.template.Template;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Registry represents a store of descriptions. A description contains a component in the
 * microservice as well as metadata information.
 * <p/>
 * A registry is also responsible to process descriptions from external sources into an
 * internal runtime representation. Thus registry defines a type for the external, raw
 * representation (R) that will be transformed into an internal, runtime representation
 * type (D) with the help of a transformer class (T).
 * <p/>
 * @param <D> the internal, runtime type of the description
 * @param <R> the external, raw type of the description
 * @param <T> the type of the transformer
 */
public interface Registry<D, R extends Transformable<D, T>, T extends Transformer> {

    /**
     * Return the base class for the raw descriptions
     *
     * @return raw description class
     */
    Class<R> getRawDescriptionClass();

    /**
     * Return a transformer instance capable of transforming a raw description into a description
     *
     * @return a transformer instance
     */
    T getTransformer();

    /**
     * Return a resolver instance to resolve expressions within descriptions
     *
     * @return a resolver instance
     */
    Resolver getResolver();

    /**
     * Return the number of concurrent threads to be used to process raw descriptions
     *
     * @return number of threads
     */
    default int getConcurrency() {
        return 1;
    }

    /**
     * Return the description associated with the name.
     *
     * @param descriptionName the description name
     * @return the description or null if no description by that name
     */
    D getDescription(String descriptionName);

    /**
     * Get resources within the application that match the provided location pattern
     *
     * @param locationPattern a pattern
     * @return a stream of templates
     */
    default Stream<Template> getResources(String locationPattern) {
        return getResources(locationPattern, false);
    }

    /**
     * Get resources within the application that match the provided location pattern, processing them
     * in parallel if needed to improve performance.
     *
     * @param locationPattern a pattern
     * @param parallel a flag indicating parallel processing is desired
     * @return a stream of templates
     */
    default Stream<Template> getResources(String locationPattern, boolean parallel) {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        try {
            Resource[] resources = resolver.getResources(locationPattern);
            List<TemplateInput> inputs = new ArrayList<>();
            for (Resource resource : resources) {
                inputs.add(new TemplateInput(resource.getFilename(), resource.getInputStream()));
            }
            InputSpliterator spliterator = new InputSpliterator(inputs, getConcurrency());
            return StreamSupport.stream(spliterator, parallel);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Get the resource identified by the location
     *
     * @param location the resource location
     * @return a template
     */
    default Template getResource(String location) {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        Resource resource = resolver.getResource(location);
        try {
            return Template.from(resource.getFilename(), resource.getInputStream());
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Get a raw description from a template using a template transformer
     *
     * @param template a template
     * @param templateTransformer a template transformer
     * @return a raw description
     */
    default R getRawDescription(Template template, TemplateTransformer<R> templateTransformer) {
        try {
            return templateTransformer.transform(template);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Transform a raw description into a description using the transformer associated with this
     * registry.
     *
     * @param rawDescription a raw description
     * @return a description
     */
    default D transformDescription(R rawDescription) {
        return rawDescription.accept(getTransformer());
    }
}
