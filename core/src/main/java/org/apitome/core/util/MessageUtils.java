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
