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
import org.apitome.core.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOperation<Request, Response> implements Operation<Request, Response> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Map<ActionKey<?>, Action<Request, ?>> actionMap;

    private ServiceManager serviceManager;

    public AbstractOperation() {
        this.actionMap = new HashMap<>();
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public <Result> void addAction(ActionKey<Result> actionKey, Action<Request, Result> action) {
        actionMap.put(actionKey, action);
    }

    @Override
    public <Result> Action<Request, Result> getAction(ActionKey<Result> actionKey) {
        Action<Request, ?> actionObj = actionMap.get(actionKey);
        return actionObj.getClass().cast(actionObj);
    }
}
