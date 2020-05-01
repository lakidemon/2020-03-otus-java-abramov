package ru.otus.logger.proxy;

import ru.otus.logger.Log;
import ru.otus.logger.object.FinancesHandler;
import ru.otus.logger.object.LoggableHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HandlerFactory {

    public static LoggableHandler createFinancesHandler() {
        return (LoggableHandler) createLoggingProxy(FinancesHandler.class, new Class[] { LoggableHandler.class });
    }

    public static Object createLoggingProxy(Class<?> instanceClass, Class[] interfaces) {
        return Proxy.newProxyInstance(instanceClass.getClassLoader(), interfaces,
                new LoggableInterceptor(instanceClass, interfaces));
    }

    private static class LoggableInterceptor implements InvocationHandler {
        private Set<Method> methodsToLog = new HashSet<>();
        private Object instance;

        public LoggableInterceptor(Class<?> instance, Class[] interfaces) {
            try {
                this.instance = instance.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No default constructor for " + instance.getName());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException("Cannot instantiate " + instance.getName());
            }

            Arrays.stream(instance.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Log.class))
                    .map(method -> toInterfaceMethod(method, interfaces)) // методы не из интерфейса не перехватываются
                    .filter(Objects::nonNull)
                    .forEach(methodsToLog::add);
        }

        private Method toInterfaceMethod(Method method, Class[] interfaces) {
            for (Class aClass : interfaces) {
                try {
                    return aClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
            return null;
        }

        @Override
        public Object invoke(Object p, Method method, Object[] args) throws Throwable {
            if (methodsToLog.contains(method)) {
                System.out.println(method.getName() + " called with args " + Arrays.toString(args));
            }
            return method.invoke(instance, args);
        }
    }

}
