package ru.otus.testing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
public class TestCase {
    private Class<?> testClass;
    private List<Method> before;
    private Method test;
    private List<Method> after;

    public TestResult runTest() {
        Object instance;
        try {
            instance = createTestInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object", e);
        }

        String name = getTestName();
        Throwable failure = null;
        long deltaTime = 0;
        try {
            runMethods(instance, before);
            long start = System.currentTimeMillis();
            try {
                runMethod(instance, test);
            } finally {
                deltaTime = System.currentTimeMillis() - start;
            }
            runMethods(instance, after);
        } catch (AssertionError | IllegalAccessException e) {
            failure = e;
        } catch (InvocationTargetException e) {
            failure = e.getTargetException();
        }

        return new TestResult(name, deltaTime, failure);
    }

    public String getTestName() {
        return test.getName();
    }

    private void runMethod(Object instance, Method method) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance);
    }

    private void runMethods(Object instance, Collection<Method> methods)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            runMethod(instance, method);
        }
    }

    private Object createTestInstance()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return testClass.getConstructor().newInstance();
    }
}
