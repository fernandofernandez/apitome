package org.apitome.core.logging;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public interface LogKey {

    String getId();

    ResourceBundle getLogKeys();

    default String getKeyName() {
        String keyName = getId();
        ResourceBundle logKeys = getLogKeys();
        try {
            return logKeys.getString(keyName);
        } catch (MissingResourceException mre) {
            return keyName;
        }
    }
}
