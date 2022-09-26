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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DefaultServiceManager implements ServiceManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Executor actionExecutor;

    private final Map<ServiceKey<?>, Service> serviceMap;

    public DefaultServiceManager() {
        this.serviceMap = new HashMap<>();
        this.actionExecutor = Executors.newFixedThreadPool(5, new ActionThreadFactory());
    }

    public DefaultServiceManager(Executor actionExecutor) {
        this.serviceMap = new HashMap<>();
        this.actionExecutor = actionExecutor;
    }

    @Override
    public <S extends Service> void addService(ServiceKey<S> serviceKey, S service) {
        serviceMap.put(serviceKey, service);
    }

    @Override
    public <S extends Service> S getService(ServiceKey<S> serviceKey) {
        return serviceKey.getType().cast(serviceMap.get(serviceKey));
    }

    @Override
    public Executor getExecutor() {
        return actionExecutor;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    public static class ActionThreadFactory implements ThreadFactory {

        private int threadNum;

        @Override
        public Thread newThread(Runnable r) {
            threadNum++;
            return new Thread(r, String.format("action-thread-%d", threadNum));
        }
    }
}
