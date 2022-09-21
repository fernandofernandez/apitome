package org.apitome.core.action;

/**
 * ExceptionHandler is a functional interface provided for Action callers to override or extend an Action's default
 * error handling.
 */
@FunctionalInterface
public interface ExceptionHandler {

    void handleException(Action action, Exception exception);
}
