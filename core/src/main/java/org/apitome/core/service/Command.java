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

package org.apitome.core.service;

/**
 * A command to be run using a service
 * <p/>
 * The Command functional interface is used to allow the calling of service beans from operations and actions
 * in a type-safe manner.
 *
 * @param <S> the service type
 * @param <R> the return type of the command
 */
@FunctionalInterface
public interface Command<S, R> {

    /**
     * Run the command using the service
     * @param service the service
     * @return the value returned by the command
     */
    R run(S service);
}
