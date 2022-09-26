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

package org.apitome.core.action;

import org.apitome.core.error.ServiceException;
import org.apitome.core.logging.LogFields;
import org.apitome.core.logging.LoggerAware;
import org.apitome.core.model.Context;
import org.apitome.core.service.Command;
import org.apitome.core.service.Service;
import org.apitome.core.service.ServiceKey;
import org.apitome.core.service.ServiceManager;

import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static org.apitome.core.logging.OpLogEvent.ACTION_COMPLETED;
import static org.apitome.core.logging.OpLogEvent.ACTION_EXCEPTION;
import static org.apitome.core.logging.OpLogEvent.ACTION_TIMEOUT;
import static org.apitome.core.logging.OpLogKey.ACTION_CLASS;
import static org.apitome.core.logging.OpLogKey.ELAPSED_TIME;

/**
 * Action represents an action against a downstream service.
 * <p/>
 * @param <Request> the request type serviced by the API
 * @param <Result> the result type returned by this action
 */
public interface Action<Request, Result> extends LoggerAware {

    /**
     * Perform the action
     *
     * @param request a request
     * @param context the context for this action
     * @return
     */
    default Result perform(Request request, Context context) {
        return perform(request, context, getTimeoutHandler());
    }

    /**
     * Perform the action, overriding the action's timeout handling
     *
     * @param request a request
     * @param context the context for this action
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @return
     */
    default Result perform(Request request, Context context, ExceptionHandler timeoutHandler) {
        return perform(request, context, timeoutHandler, getExceptionHandler());
    }

    /**
     * Perform the action, overriding the action's timeout handling and its exception handling
     *
     * @param request a request
     * @param context the context for this action
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @param exceptionHandler an exception handler that overrides the action's general error handling
     * @return
     */
    default Result perform(Request request, Context context, ExceptionHandler timeoutHandler, ExceptionHandler exceptionHandler) {
        long start = System.currentTimeMillis();
        try {
            Result result = invoke(request, context);
            long elapsedTime = System.currentTimeMillis() - start;
            LogFields logFields = LogFields.builder(ACTION_COMPLETED)
                    .addKeyValue(ACTION_CLASS, this.getClass().getSimpleName())
                    .addKeyValue(ELAPSED_TIME, elapsedTime)
                    .build();
            logInfo(logFields);
            return result;
        } catch (TimeoutException | SocketTimeoutException te) {
            long elapsedTime = System.currentTimeMillis() - start;
            LogFields logFields = LogFields.builder(ACTION_TIMEOUT)
                    .addKeyValue(ACTION_CLASS, this.getClass().getSimpleName())
                    .addKeyValue(ELAPSED_TIME, elapsedTime)
                    .build();
            logError(logFields, te);
            timeoutHandler.handleException(this, te);
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - start;
            LogFields logFields = LogFields.builder(ACTION_EXCEPTION)
                    .addKeyValue(ACTION_CLASS, this.getClass().getSimpleName())
                    .addKeyValue(ELAPSED_TIME, elapsedTime)
                    .build();
            logError(logFields, e);
            exceptionHandler.handleException(this, e);
        }
        return null;
    }

    /**
     * Perform the action asynchronously
     *
     * @param request a request
     * @param context the context for this action
     * @return
     */
    default CompletableFuture<Result> performAsync(Request request, Context context) {
        return performAsync(request, context, getTimeoutHandler());
    }

    /**
     * Perform the action asynchronously, overriding the action's timeout handling
     *
     * @param request a request
     * @param context the context for this action
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @return a CompletableFuture for the action's result
     */
    default CompletableFuture<Result> performAsync(Request request, Context context, ExceptionHandler timeoutHandler) {
        return performAsync(request, context, timeoutHandler, getExceptionHandler());
    }

    /**
     * Perform the action asynchronously, overriding the action's timeout handling and its exception handling
     *
     * @param request a request
     * @param context the context for this action
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @param exceptionHandler an exception handler that overrides the action's general error handling
     * @return a CompletableFuture for the action's result
     */
    default CompletableFuture<Result> performAsync(Request request, Context context, ExceptionHandler timeoutHandler,
                                                   ExceptionHandler exceptionHandler) {
        BiFunction<Action<Request, Result>, Request,Result> actionFunction = (action, req) -> action.perform(req,
                context, timeoutHandler, exceptionHandler);
        ServiceManager serviceManager = getServiceManager();
        return serviceManager.invokeAction(this, request, actionFunction);
    }

    /**
     * Invoke the action's main logic
     * <p/>
     *
     * @param request a request
     * @return the action's result
     * @throws TimeoutException
     * @throws SocketTimeoutException
     */
    Result invoke(Request request, Context context) throws TimeoutException, SocketTimeoutException;

    /**
     * Invoke a service identified by the service key
     *
     * @param serviceKey the service key that identifies the service to be invoked
     * @param command the command to be executed by the service
     * @return the value returned by the command
     * @param <S> the type of the service
     * @param <R> the type of the command's returned value
     */
    default <S extends Service, R> R invokeService(ServiceKey<S> serviceKey, Command<S, R> command) {
        return getServiceManager().invokeService(serviceKey, command);
    }

    /**
     * Handle an exception and transform it into a {@link ServiceException}
     * <p/>
     * This method defines the default handling of exceptions for this action. The default {@link ExceptionHandler}
     * for timeouts and general exceptions call this method to obtain a {@link ServiceException} to throw. Operations
     * performing this action and overriding the default ExceptionHandlers can fall back to this method to extend
     * the action's error handling.
     *
     * @param e an exception
     * @return a service exception
     */
    ServiceException handleException(Exception e);

    /**
     * Set the {@link ServiceManager} for this action
     *
     * @param serviceManager the service manager
     */
    void setServiceManager(ServiceManager serviceManager);

    /**
     * Return the {@link ServiceManager} for this action
     *
     * @return the service manager
     */
    ServiceManager getServiceManager();

    /**
     * Return the timeout exception handler for this action
     * <p/>
     * The default implementation throws the {@link ServiceException} returned by the {@link #handleException(Exception)}
     * <p/>
     * @return the timeout exception handler
     */
    default ExceptionHandler getTimeoutHandler() {
        return (a, e) -> {throw handleException(e);};
    }

    /**
     * Return the general exception handler for this action
     * <p/>
     * the default implementation bubbles up any {@link ServiceException} thrown from the service layer and if not
     * a ServiceException, throws the {@link ServiceException} returned by the {@link #handleException(Exception)}
     * <p/>
     * @return the general exception handler
     */
    default ExceptionHandler getExceptionHandler() {
        return (a, e) -> {
            // Let ServiceExceptions from the Service layer bubble up
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            throw handleException(e);
        };
    }
}