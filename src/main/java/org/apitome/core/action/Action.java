package org.apitome.core.action;

import org.apitome.core.error.ServiceException;
import org.apitome.core.logging.LogFields;
import org.apitome.core.logging.LoggerAware;
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

public interface Action<Request, Result> extends LoggerAware {

    default Result perform(Request request) {
        return perform(request, getTimeoutHandler());
    }

    default Result perform(Request request, ExceptionHandler timeoutHandler) {
        return perform(request, timeoutHandler, getExceptionHandler());
    }

    default Result perform(Request request, ExceptionHandler timeoutHandler, ExceptionHandler exceptionHandler) {
        long start = System.currentTimeMillis();
        try {
            Result result = invoke(request);
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

    default CompletableFuture<Result> performAsync(Request request) {
        return performAsync(request, getTimeoutHandler());
    }

    default CompletableFuture<Result> performAsync(Request request, ExceptionHandler timeoutHandler) {
        return performAsync(request, timeoutHandler, getExceptionHandler());
    }

    default CompletableFuture<Result> performAsync(Request request, ExceptionHandler timeoutHandler, ExceptionHandler exceptionHandler) {
        BiFunction<Action<Request, Result>, Request,Result> actionFunction = (action, req) -> action.perform(req, timeoutHandler, exceptionHandler);
        ServiceManager serviceManager = getServiceManager();
        return serviceManager.invokeAction(this, request, actionFunction);
    }

    Result invoke(Request request) throws TimeoutException, SocketTimeoutException;
    
    default <S extends Service, R> R invokeService(ServiceKey<S> serviceKey, Command<S, R> command) {
        return getServiceManager().invokeService(serviceKey, command);
    }

    ServiceException handleException(Exception e);

    void setServiceManager(ServiceManager serviceManager);

    ServiceManager getServiceManager();

    default ExceptionHandler getTimeoutHandler() {
        return (a, e) -> {throw handleException(e);};
    }

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