package ru.otus.logger.proxy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.otus.logger.Log;
import ru.otus.logger.object.LoggableHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HandlerFactory {

    public static LoggableHandler createLoggableHandler(Class<? extends LoggableHandler> clazz) {
        return createLoggableHandler(clazz, null);
    }

    public static LoggableHandler createLoggableHandler(Class<? extends LoggableHandler> clazz,
            ConstructorParams consParams) {
        return (LoggableHandler) createLoggingProxy(clazz, new Class[] { LoggableHandler.class }, consParams);
    }

    public static Object createLoggingProxy(Class<?> instanceClass, Class[] interfaces, ConstructorParams consParams) {
        return Proxy.newProxyInstance(instanceClass.getClassLoader(), interfaces,
                new LoggableInterceptor(instanceClass, interfaces, consParams));
    }

    private static class LoggableInterceptor implements InvocationHandler {
        private Set<Method> methodsToLog = new HashSet<>();
        private Object instance;

        public LoggableInterceptor(Class<?> instance, Class[] interfaces, ConstructorParams consParams) {
            try {
                this.instance = (consParams != null ?
                        instance.getDeclaredConstructor(consParams.getParameterTypes()) :
                        instance.getConstructor()).newInstance(
                        consParams != null ? consParams.getParameterValues() : new Objects[0]);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot find constructor for " + instance.getName(), e);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException("Cannot instantiate " + instance.getName(), e);
            }

            Arrays.stream(instance.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Log.class))
                    .map(method -> toInterfaceMethod(method, interfaces)) // методы не из интерфейса не перехватываются
                    .filter(Objects::nonNull)
                    .forEach(methodsToLog::add);
        }

        @Override
        public Object invoke(Object p, Method method, Object[] args) throws Throwable {
            if (methodsToLog.contains(method)) {
                System.out.println(method.getName() + " called with args " + Arrays.toString(args));
            }
            return method.invoke(instance, args);
        }

        private Method toInterfaceMethod(Method method, Class[] interfaces) {
            for (Class aClass : interfaces) {
                try {
                    return aClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                }
            }
            return null;
        }

    }

}
