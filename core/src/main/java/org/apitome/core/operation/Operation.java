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

package org.apitome.core.operation;

import org.apitome.core.action.Action;
import org.apitome.core.action.ActionKey;
import org.apitome.core.action.ExceptionHandler;
import org.apitome.core.logging.LoggerAware;
import org.apitome.core.model.Context;
import org.apitome.core.service.Command;
import org.apitome.core.service.Service;
import org.apitome.core.service.ServiceKey;
import org.apitome.core.service.ServiceManager;

import java.util.concurrent.CompletableFuture;

/**
 * Operation represents a process driving one or more actions in order to
 * accomplish a unit of work.
 * <p/>
 * The process may be generalized by decoupling the process flow from actions so
 * that the process can be reused with different sets of actions. Yet operations are
 * coupled to its actions by their return type and can perform them in a type-safe
 * manner. This way, different actions can safely be referenced by the operation
 * using an ActionKey.
 * <p/>
 * In order to drive the actions effectively, an operation may extend or completely
 * override the error handling defined in the actions. In order to do that, the
 * operation performs an action passing {@code ExceptionHandler} functional
 * interfaces that are invoked by the action instead of its own. Extending the
 * action's error handling can be accomplished by calling the Action parameter
 * passed to the functional interface. See {@code Action} for details.
 *
 * @param <Request> the request type serviced by the API
 * @param <Response> the response type to be returned by the API
 *
 * @author Fernando Fernandez
 */
public interface Operation<Request, Response> extends LoggerAware {

    /**
     * Execute the operation's logic
     * @param request a request sent to this API
     * @param context the context for the operation
     * @return the response from the API
     */
    Response execute(Request request, Context context);

    /**
     * Set the {@link ServiceManager} for this operation
     * @param serviceManager a ServiceManager instance
     */
    void setServiceManager(ServiceManager serviceManager);

    /**
     * Return the {@link ServiceManager} for this operation
     * @return the service manager
     */
    ServiceManager getServiceManager();

    /**
     * Add an {@link Action} to this operation
     * @param actionKey the key that identifies this action to the operation
     * @param action the action
     * @param <Result> the result type of the action result
     */
    <Result> void addAction(ActionKey<Result> actionKey, Action<Request, Result> action);

    /**
     * Return the action identified by the action key
     * @param actionKey the action key of the action
     * @return an action
     * @param <Result> the target result type of the action
     */
    <Result> Action<Request, Result> getAction(ActionKey<Result> actionKey);

    /**
     * Perform the action identified by the action key
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for this operation
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> Result performAction(ActionKey<Result> actionKey, Request request, Context context) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request, context);
    }

    /**
     * Perform the action identified by the action key, overriding the action's timeout handling
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for this operation
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> Result performAction(ActionKey<Result> actionKey, Request request, Context context,
                                          ExceptionHandler timeoutHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request, context, timeoutHandler);
    }

    /**
     * Perform the action identified by the action key, overriding the action's timeout handling and
     * its exception handling
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for this operation
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @param errorHandler an exception handler that overrides the action's general error handling
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> Result performAction(ActionKey<Result> actionKey, Request request, Context context,
                                          ExceptionHandler timeoutHandler, ExceptionHandler errorHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request, context, timeoutHandler, errorHandler);
    }

    /**
     * Perform the action identified by the action key asynchronously
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for the operation
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request,
                                                                  Context context) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request, context);
    }

    /**
     * Perform the action identified by the action key asynchronously, overriding the action's timeout handling
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for the operation
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request,
                                                                  Context context, ExceptionHandler timeoutHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request, context, timeoutHandler);
    }

    /**
     * Perform the action identified by the action key asynchronously, overriding the action's timeout handling
     * and its exception handling
     *
     * @param actionKey the action key that identifies the action to be performed
     * @param request a request
     * @param context the context for the operation
     * @param timeoutHandler an exception handler that overrides the action's timeout handling
     * @param errorHandler an exception handler that overrides the action's general error handling
     * @return the action's result
     * @param <Result> the target result type of the action
     */
    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request,
                                                                  Context context, ExceptionHandler timeoutHandler,
                                                                  ExceptionHandler errorHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request, context, timeoutHandler, errorHandler);
    }

    /**
     * Invoke the service identified by the service key
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
}