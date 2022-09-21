package org.apitome.core.operation;

import org.apitome.core.action.Action;
import org.apitome.core.action.ActionKey;
import org.apitome.core.action.ExceptionHandler;
import org.apitome.core.logging.LoggerAware;
import org.apitome.core.service.ServiceManager;

import java.util.concurrent.CompletableFuture;

public interface Operation<Request, Response> extends LoggerAware {

    Response execute(Request request);

    void setServiceManager(ServiceManager serviceManager);

    ServiceManager getServiceManager();

    <Result> void addAction(ActionKey<Result> actionKey, Action<Request, Result> action);

    <Result> Action<Request, Result> getAction(ActionKey<Result> actionType);

    default <Result> Result performAction(ActionKey<Result> actionKey, Request request) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request);
    }

    default <Result> Result performAction(ActionKey<Result> actionKey, Request request,
                                          ExceptionHandler timeoutHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request, timeoutHandler);
    }

    default <Result> Result performAction(ActionKey<Result> actionKey, Request request,
                                          ExceptionHandler timeoutHandler, ExceptionHandler errorHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.perform(request, timeoutHandler, errorHandler);
    }

    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request);
    }

    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request,
                                          ExceptionHandler timeoutHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request, timeoutHandler);
    }

    default <Result> CompletableFuture<Result> performActionAsync(ActionKey<Result> actionKey, Request request,
                                          ExceptionHandler timeoutHandler, ExceptionHandler errorHandler) {
        Action<Request, Result> action = getAction(actionKey);
        return action.performAsync(request, timeoutHandler, errorHandler);
    }
}