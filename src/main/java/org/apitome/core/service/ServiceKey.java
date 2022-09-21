package org.apitome.core.service;

import org.apitome.core.model.TypeKey;

public class ServiceKey<S extends Service> extends TypeKey<S> {

    public ServiceKey(String name, Class<S> type) {
        super(name, type);
    }
}
