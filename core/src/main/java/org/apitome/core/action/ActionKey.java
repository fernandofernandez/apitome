package org.apitome.core.action;

import org.apitome.core.model.TypeKey;

public class ActionKey<R> extends TypeKey<R> {

    public ActionKey(String name, Class<R> type) {
        super(name, type);
    }
}
