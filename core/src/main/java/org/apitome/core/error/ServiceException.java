package org.apitome.core.error;

public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(Exception e) {
        super(e);
    }
}
