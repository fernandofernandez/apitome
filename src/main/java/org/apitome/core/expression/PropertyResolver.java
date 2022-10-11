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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.MapELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

public class PropertyResolver extends ELResolver {

    private final ELResolver delegate = new MapELResolver();

    private final Map<Object, Object> propertyMap;

    public PropertyResolver(Map<Object, Object> propertyMap) {
        this.propertyMap = propertyMap;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            base = propertyMap;
        }
        return delegate.getValue(context, base, property);
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null) {
            base = propertyMap;
        }
        return delegate.getType(context, base, property);
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null) {
            base = propertyMap;
        }
        delegate.setValue(context, base, property, value);
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null) {
            base = propertyMap;
        }
        return delegate.isReadOnly(context, base, property);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            base = propertyMap;
        }
        return delegate.getFeatureDescriptors(context, base);
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            base = propertyMap;
        }
        return delegate.getCommonPropertyType(context, base);
    }
}
