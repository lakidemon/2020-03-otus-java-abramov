package ru.otus.testing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.testing.annotations.After;
import ru.otus.testing.annotations.Before;
import ru.otus.testing.annotations.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class TestsContainer {
    private final Class<?> testClass;

    private List<TestCase> tests = new ArrayList<>();

    public void collectTests() {
        var before = new ArrayList<Method>();
        var after = new ArrayList<Method>();

        for (var method : testClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers())
                    || method.getReturnType() != Void.TYPE)
                continue;

            if (method.isAnnotationPresent(Before.class)) {
                before.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                after.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                tests.add(new TestCase(testClass, before, method, after));
            } else
                continue;

            method.setAccessible(true);
        }
    }

    public List<TestResult> startTests(TestProgress progressHandler) {
        if (tests.isEmpty()) {
            throw new IllegalStateException("No tests to run");
        }

        var results = new ArrayList<TestResult>();
        for (TestCase test : tests) {
            if (progressHandler != null) {
                progressHandler.onTestBegin(test);
            }
            TestResult result = test.runTest();
            results.add(result);
            if (progressHandler != null) {
                progressHandler.onTestFinish(result);
            }
        }
        return results;
    }

    public interface TestProgress {

        void onTestBegin(TestCase test);

        void onTestFinish(TestResult result);

    }
}
