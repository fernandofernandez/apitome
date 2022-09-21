package org.apitome.core.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageUtils {

    public static String getMessage(String messageId, String defaultMessageId, ResourceBundle resourceBundle, String... parms) {
        try {
            String message = resourceBundle.getString(messageId);
            return MessageFormat.format(message, (Object[]) parms);
        } catch (MissingResourceException missingResourceException) {
            if (defaultMessageId == null) {
                throw missingResourceException;
            }
            String message = resourceBundle.getString(defaultMessageId);
            return MessageFormat.format(message, (Object[]) parms);
        }
    }
}
