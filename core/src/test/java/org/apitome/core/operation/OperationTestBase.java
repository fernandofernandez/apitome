package org.apitome.core.operation;

import org.apitome.core.model.Context;
import org.apitome.core.model.OpContext;
import org.apitome.core.model.TestRequest;
import org.apitome.core.service.DefaultServiceManager;
import org.apitome.core.service.ServiceManager;
import org.apitome.core.service.TestIntegerService;
import org.apitome.core.service.TestStringService;
import org.junit.jupiter.api.BeforeEach;

import static org.apitome.core.service.TestIntegerService.INTEGER_SERVICE;
import static org.apitome.core.service.TestStringService.STRING_SERVICE;
import static org.junit.jupiter.api.Assertions.fail;

public class OperationTestBase {

    protected Context context;

    protected TestRequest request;

    protected ServiceManager serviceManager;

    @BeforeEach
    public void baseSetup() {
        this.serviceManager = new DefaultServiceManager();
        serviceManager.addService(STRING_SERVICE, new TestStringService());
        serviceManager.addService(INTEGER_SERVICE, new TestIntegerService());
        this.request = new TestRequest();
        this.context = new OpContext();
    }

    public <R, E extends Exception> E assertException(Operation<R, ?> operation, R request, Context context, Class<E> exceptionClass) {
        try {
            operation.execute(request, context);
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
}
