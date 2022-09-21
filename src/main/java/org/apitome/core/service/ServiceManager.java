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

public interface ServiceManager extends LoggerAware {

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

    Executor getExecutor();

    default <Result, Request> CompletableFuture<Result> invokeAction(Action<Request,Result> action, Request request,
                                                                     BiFunction<Action<Request, Result>, Request,Result> asyncFunction) {
        return CompletableFuture.supplyAsync(() -> asyncFunction.apply(action, request), getExecutor());
    }
}
