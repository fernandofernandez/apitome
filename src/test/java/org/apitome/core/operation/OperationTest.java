package org.apitome.core.operation;

import org.apitome.core.action.AbstractAction;
import org.apitome.core.action.ActionKey;
import org.apitome.core.action.ExceptionHandler;
import org.apitome.core.error.ServiceException;
import org.apitome.core.model.Context;
import org.apitome.core.service.TestIntegerService;
import org.apitome.core.service.TestStringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apitome.core.service.TestIntegerService.INTEGER_SERVICE;
import static org.apitome.core.service.TestStringService.STRING_SERVICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationTest extends OperationTestBase {

    private static final ActionKey<Integer> ACTION_ONE = new ActionKey<>("actionOne", Integer.class);

    private static final ActionKey<String> ACTION_TWO = new ActionKey<>("actionTwo", String.class);

    private ExceptionHandler timeoutHandler;

    private ExceptionHandler exceptionHandler;

    private boolean runAsync;

    private TestOperation operation;

    private TestActionOne actionOne;

    private TestActionTwo actionTwo;

    @BeforeEach
    public void setup() {
        this.runAsync = false;
        this.timeoutHandler = null;
        this.exceptionHandler = null;
        this.operation = new TestOperation();
        operation.setServiceManager(serviceManager);
        this.actionOne = new TestActionOne();
        actionOne.setServiceManager(serviceManager);
        this.actionTwo = new TestActionTwo();
        actionTwo.setServiceManager(serviceManager);
        operation.addAction(ACTION_ONE, actionOne);
        operation.addAction(ACTION_TWO, actionTwo);
    }

    @Test
    public void testNormalOperation() {
        request.setIntValue(7);
        request.setStrValue("Fernando");
        TestResponse result = operation.execute(request, context);
        assertEquals(107, result.getIntValue());
        assertEquals("Hello Fernando", result.getStrValue());
    }

    @Test
    public void testAsyncOperation() {
        this.runAsync = true;
        request.setIntValue(7);
        request.setStrValue("Fernando");
        TestResponse result = operation.execute(request, context);
        assertEquals(107, result.getIntValue());
        assertEquals("Hello Fernando", result.getStrValue());
    }

    @Test
    public void testTimeoutInOperation() {
        TestIntegerService integerService = mock(TestIntegerService.class);
        serviceManager.addService(INTEGER_SERVICE, integerService);
        SocketTimeoutException timeout = new SocketTimeoutException("mock socket timeout");
        RuntimeException rte = new RuntimeException(timeout);
        // Mockito cannot throw checked exceptions so we use a RuntimeException
        when(integerService.sumIntegers(any())).thenThrow(rte);
        AtomicBoolean timeoutHandlerInvoked = new AtomicBoolean(false);
        this.timeoutHandler = (a, e) -> {timeoutHandlerInvoked.set(true); throw a.handleException(e);};
        ServiceException result = assertException(operation, request, context, ServiceException.class);
        assertNotNull(result);
        assertTrue(timeoutHandlerInvoked.get(), "Operation timeoutHandler not invoked");
        assertSame(timeout, result.getCause());
    }

    @Test
    public void testExceptionInOperation() {
        TestIntegerService integerService = mock(TestIntegerService.class);
        serviceManager.addService(INTEGER_SERVICE, integerService);
        IllegalArgumentException iae = new IllegalArgumentException();
        when(integerService.sumIntegers(any())).thenThrow(iae);
        AtomicBoolean timeoutHandlerInvoked = new AtomicBoolean(false);
        AtomicBoolean exceptionHandlerInvoked = new AtomicBoolean(false);
        this.timeoutHandler = (a, e) -> {timeoutHandlerInvoked.set(true); throw a.handleException(e);};
        this.exceptionHandler = (a, e) -> {exceptionHandlerInvoked.set(true); throw (RuntimeException) e;};
        IllegalArgumentException result = assertException(operation, request, context, IllegalArgumentException.class);
        assertNotNull(result);
        assertFalse(timeoutHandlerInvoked.get(), "Operation timeoutHandler was invoked");
        assertTrue(exceptionHandlerInvoked.get(), "Operation exceptionHandler not invoked");
    }

    @Test
    public void testTimeoutInAsyncOperation() {
        TestStringService stringService = mock(TestStringService.class);
        serviceManager.addService(STRING_SERVICE, stringService);
        SocketTimeoutException timeout = new SocketTimeoutException("mock socket timeout");
        RuntimeException rte = new RuntimeException(timeout);
        // Mockito cannot throw checked exceptions so we use a RuntimeException
        when(stringService.appendStrings(any())).thenThrow(rte);
        AtomicBoolean timeoutHandlerInvoked = new AtomicBoolean(false);
        this.timeoutHandler = (a, e) -> {timeoutHandlerInvoked.set(true); throw a.handleException(e);};
        this.runAsync = true;
        request.setIntValue(7);
        request.setStrValue("Fernando");
        RuntimeException result = assertException(operation, request, context, RuntimeException.class);
        assertNotNull(result);
        assertTrue(timeoutHandlerInvoked.get(), "Operation timeoutHandler not invoked");
        assertSame(timeout, result.getCause().getCause());
    }

    @Test
    public void testExceptionInAsyncOperation() {
        TestStringService stringService = mock(TestStringService.class);
        serviceManager.addService(STRING_SERVICE, stringService);
        IllegalArgumentException iae = new IllegalArgumentException();
        // Mockito cannot throw checked exceptions so we use a RuntimeException
        when(stringService.appendStrings(any())).thenThrow(iae);
        AtomicBoolean timeoutHandlerInvoked = new AtomicBoolean(false);
        AtomicBoolean exceptionHandlerInvoked = new AtomicBoolean(false);
        this.timeoutHandler = (a, e) -> {timeoutHandlerInvoked.set(true); throw a.handleException(e);};
        this.exceptionHandler = (a, e) -> {exceptionHandlerInvoked.set(true); throw (RuntimeException) e;};
        this.runAsync = true;
        request.setIntValue(7);
        request.setStrValue("Fernando");
        RuntimeException result = assertException(operation, request, context, RuntimeException.class);
        assertNotNull(result);
        assertFalse(timeoutHandlerInvoked.get(), "Operation timeoutHandler was invoked");
        assertTrue(exceptionHandlerInvoked.get(), "Operation exceptionHandler not invoked");
        assertSame(iae, result.getCause());
    }

    public class TestOperation extends AbstractOperation<TestRequest, TestResponse> {

        @Override
        public TestResponse execute(TestRequest request, Context context) {
            Integer intValue;
            String strValue;
            if (runAsync) {
                intValue = performActionOneAsync(request);
            } else {
                intValue = performActionOne(request);
            }
            if (runAsync) {
                strValue = performActionTwoAsync(request);
            } else {
                strValue = performActionTwo(request);
            }
            TestResponse response = new TestResponse();
            response.setIntValue(intValue);
            response.setStrValue(strValue);
            return response;
        }

        private Integer performActionOne(TestRequest request) {
            if (timeoutHandler != null && exceptionHandler != null) {
                return performAction(ACTION_ONE, request, context, timeoutHandler, exceptionHandler);
            } else if (timeoutHandler != null && exceptionHandler == null) {
                return performAction(ACTION_ONE, request, context, timeoutHandler);
            } else {
                return performAction(ACTION_ONE, request, context);
            }
        }

        private String performActionTwo(TestRequest request) {
            if (timeoutHandler != null && exceptionHandler != null) {
                return performAction(ACTION_TWO, request, context, timeoutHandler, exceptionHandler);
            } else if (timeoutHandler != null && exceptionHandler == null) {
                return performAction(ACTION_TWO, request, context, timeoutHandler);
            } else {
                return performAction(ACTION_TWO, request, context);
            }
        }

        private Integer performActionOneAsync(TestRequest request) {
            try {
                if (timeoutHandler != null && exceptionHandler != null) {
                    return performActionAsync(ACTION_ONE, request, context, timeoutHandler, exceptionHandler).get();
                } else if (timeoutHandler != null && exceptionHandler == null) {
                    return performActionAsync(ACTION_ONE, request, context, timeoutHandler).get();
                } else {
                    return performActionAsync(ACTION_ONE, request, context).get();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        private String performActionTwoAsync(TestRequest request) {
            try {
                if (timeoutHandler != null && exceptionHandler != null) {
                    return performActionAsync(ACTION_TWO, request, context, timeoutHandler, exceptionHandler).get();
                } else if (timeoutHandler != null && exceptionHandler == null) {
                    return performActionAsync(ACTION_TWO, request, context, timeoutHandler).get();
                } else {
                    return performActionAsync(ACTION_TWO, request, context).get();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public class TestActionOne extends AbstractAction<TestRequest, Integer> {
        @Override
        public Integer invoke(TestRequest testRequest, Context context) throws TimeoutException, SocketTimeoutException {
            try {
                return invokeService(INTEGER_SERVICE, s -> s.sumIntegers(testRequest.getIntValue(), 100));
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof TimeoutException) {
                    throw (TimeoutException) cause;
                } else if (cause instanceof SocketTimeoutException) {
                    throw (SocketTimeoutException) cause;
                } else {
                    throw e;
                }
            }
        }

        @Override
        public ServiceException handleException(Exception e) {
            return new ServiceException(e);
        }
    }

    public class TestActionTwo extends AbstractAction<TestRequest, String> {
        @Override
        public String invoke(TestRequest testRequest, Context context) throws TimeoutException, SocketTimeoutException {
            try {
                return invokeService(STRING_SERVICE, s -> s.appendStrings("Hello ", testRequest.getStrValue()));
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof TimeoutException) {
                    throw (TimeoutException) cause;
                } else if (cause instanceof SocketTimeoutException) {
                    throw (SocketTimeoutException) cause;
                } else {
                    throw e;
                }
            }
        }

        @Override
        public ServiceException handleException(Exception e) {
            return new ServiceException(e);
        }
    }
}
