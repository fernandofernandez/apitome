package org.apitome.core.error;

public interface ApplicationError {

    String getId();

    String getMessage();

    String getMessage(String... args);

    String getDeveloperMessage();

    String getDeveloperMessage(String... args);
}
