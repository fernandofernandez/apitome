package org.apitome.core.service;

public class TestStringService implements Service {

    public static final ServiceKey<TestStringService> STRING_SERVICE = new ServiceKey<>("stringService", TestStringService.class);

    public String appendStrings(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }
}
