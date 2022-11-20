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

import org.apitome.core.action.Action;
import org.apitome.core.logging.LogFields;
import org.apitome.core.logging.LoggerAware;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import static org.apitome.core.logging.OpLogEvent.SERVICE_EXCEPTION;
import static org.apitome.core.logging.OpLogKey.ELAPSED_TIME;
import static org.apitome.core.logging.OpLogKey.SERVICE_CLASS;

/**
 * ServiceManager is a generic command dispatcher.
 */
public interface ServiceManager extends LoggerAware {

    /**
     * Add a service
     * @param serviceKey
     * @param service
     * @param <S>
     */
    <S extends Service> void addService(ServiceKey<S> serviceKey, S service);

    <S extends Service> S getService(ServiceKey<S> serviceKey);

    default <S extends Service, R> R invokeService(ServiceKey<S> serviceKey, Command<S, R> command) {
        long start = System.currentTimeMillis();
        String serviceName = serviceKey.getName();
        S service = getService(serviceKey);
        if (service == null) {
            throw new IllegalArgumentException("Service '" + serviceName + "' is not hosted in this ServiceManager");
        }
        try {
            return command.run(service);
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - start;
            LogFields logFields = LogFields.builder(SERVICE_EXCEPTION)
                    .addKeyValue(SERVICE_CLASS, service.getClass().getSimpleName())
                    .addKeyValue(ELAPSED_TIME, elapsedTime)
                    .build();
            logError(logFields, e);
            throw e;
        }
    }

    /**
     * Return the {@link java.util.concurrent.Executor} associated with this ServiceManager
     * <p/>
     * @return the Executor instance
     */
    Executor getExecutor();

    /**
     * Invoke an action asynchronously
     * <p/>
     *
     * @param action
     * @param request
     * @param asyncFunction
     * @return
     * @param <Result>
     * @param <Request>
     */
    default <Result, Request> CompletableFuture<Result> invokeAction(Action<Request,Result> action, Request request,
                                                                     BiFunction<Action<Request, Result>, Request,Result> asyncFunction) {
        return CompletableFuture.supplyAsync(() -> asyncFunction.apply(action, request), getExecutor());
    }
}
