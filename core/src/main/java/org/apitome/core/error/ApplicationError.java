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

package org.apitome.core.error;

/**
 * ApplicationError represents an error within the microservice
 * <p/>
 * An application error has an internal identifier and two messages associated with it: a
 * message intended for the user, which can be potentially displayed in a user interface;
 * and a message intended for developers, which carries application details that would be
 * meaningless to a user.
 */
public interface ApplicationError {

    String getId();

    String getMessage();

    String getMessage(String... args);

    String getDeveloperMessage();

    String getDeveloperMessage(String... args);
}
