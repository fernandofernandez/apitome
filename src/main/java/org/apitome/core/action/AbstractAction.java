package org.apitome.core.action;

import org.apitome.core.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction<Request, Result> implements Action<Request, Result> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ServiceManager serviceManager;

    @Override
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
