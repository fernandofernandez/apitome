package org.apitome.core.error;

import org.apitome.core.util.MessageUtils;

import java.util.ResourceBundle;

public interface ErrorCode extends ApplicationError {

    default String getMessage() {
        return MessageUtils.getMessage(getId(), getDefaultMessageId(), getMessages());
    }

    default String getMessage(String... parms) {
        return MessageUtils.getMessage(getId(), getDefaultMessageId(), getMessages(), parms);
    }

    default String getDeveloperMessage() {
        return MessageUtils.getMessage(getId(), null, getDeveloperMessages());
    }

    default String getDeveloperMessage(String... parms) {
        return MessageUtils.getMessage(getId(), null, getDeveloperMessages(), parms);
    }

    String getDefaultMessageId();

    ResourceBundle getMessages();

    ResourceBundle getDeveloperMessages();

}
