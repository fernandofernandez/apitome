package org.apitome.core.service;

public class TestIntegerService implements Service {

    public static final ServiceKey<TestIntegerService> INTEGER_SERVICE = new ServiceKey<>("integerService", TestIntegerService.class);

    public Integer sumIntegers(Integer... args) {
        Integer sum = Integer.valueOf(0);
        for (Integer arg : args) {
            sum += arg;
        }
        return sum;
    }

}
