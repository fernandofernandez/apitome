package org.apitome.core.logging;

import org.apitome.core.util.UtfControl;

import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public enum OpLogKey implements LogKey {

    ACTION_CLASS,
    ELAPSED_TIME,
    EVENT,
    SERVICE_CLASS;

    ResourceBundle LOG_KEYS = ResourceBundle.getBundle("log-keys", new UtfControl(StandardCharsets.UTF_8));
    @Override
    public String getId() {
        return name();
    }

    @Override
    public ResourceBundle getLogKeys() {
        return LOG_KEYS;
    }

    @Override
    public String toString() {
        return getKeyName();
    }
}
