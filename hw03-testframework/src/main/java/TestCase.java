import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
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

        String name = testClass.getName() + "_" + test.getName();
        try {
            runMethods(instance, before);
            runMethod(instance, test);
            runMethods(instance, after);
            return new TestResult(name, null);
        } catch (Exception | AssertionError e) {
            return new TestResult(name, e);
        }
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
