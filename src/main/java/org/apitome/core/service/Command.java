package org.apitome.core.service;

@FunctionalInterface
public interface Command<S, R> {

    R run(S service);
}
