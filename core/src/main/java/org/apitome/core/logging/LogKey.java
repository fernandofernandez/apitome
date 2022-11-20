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
