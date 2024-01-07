package org.apitome.core.error;

import java.util.ResourceBundle;

public enum OperationError implements ErrorCode {

    TIMEOUT_ERROR("OPERR001");

    private final String id;

    OperationError(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDefaultMessageId() {
        return "defaultMessage";
    }

    @Override
    public ResourceBundle getMessages() {
        return null;
    }

    @Override
    public ResourceBundle getDeveloperMessages() {
        return null;
    }
}
