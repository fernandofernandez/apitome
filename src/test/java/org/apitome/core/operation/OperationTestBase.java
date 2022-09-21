package org.apitome.core.operation;

import org.apitome.core.service.DefaultServiceManager;
import org.apitome.core.service.ServiceManager;
import org.apitome.core.service.TestIntegerService;
import org.apitome.core.service.TestStringService;
import org.junit.jupiter.api.BeforeEach;

import static org.apitome.core.service.TestIntegerService.INTEGER_SERVICE;
import static org.apitome.core.service.TestStringService.STRING_SERVICE;
import static org.junit.jupiter.api.Assertions.fail;

public class OperationTestBase {

    protected  TestRequest request;

    protected ServiceManager serviceManager;

    @BeforeEach
    public void baseSetup() {
        this.serviceManager = new DefaultServiceManager();
        serviceManager.addService(STRING_SERVICE, new TestStringService());
        serviceManager.addService(INTEGER_SERVICE, new TestIntegerService());
        this.request = new TestRequest();
    }

    public <R, E extends Exception> E assertException(Operation<R, ?> operation, R request, Class<E> exceptionClass) {
        try {
            operation.execute(request);
            fail("Expected exception " + exceptionClass.getCanonicalName() + " but no exception was thrown.");
        } catch (Exception e) {
            if (exceptionClass.isInstance(e)) {
                return (E) e;
            } else {
                fail("Expected exception " + exceptionClass.getCanonicalName() + " but caught " + e.getClass().getCanonicalName());
            }
        }
        return null;
    }

    public class TestRequest {

        private Integer intValue;

        private String strValue;

        public Integer getIntValue() {
            return intValue;
        }

        public void setIntValue(Integer intValue) {
            this.intValue = intValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }
    }

    public class TestResponse {

        private Integer intValue;

        private String strValue;

        public Integer getIntValue() {
            return intValue;
        }

        public void setIntValue(Integer intValue) {
            this.intValue = intValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }
    }
}
