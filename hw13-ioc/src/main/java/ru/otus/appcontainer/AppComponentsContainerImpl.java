package ru.otus.appcontainer;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.reflections8.Reflections;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.api.ContainerException;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AppComponentsContainerImpl implements AppComponentsContainer {
    private final List<ComponentProvider> discoveredProviders = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        for (Class<?> config : initialConfigClasses) {
            collectProviders(config);
        }
        instantiateComponents();
    }

    public AppComponentsContainerImpl(String... packagesToScan) {
        this(findConfigurations(packagesToScan));
    }

    private void collectProviders(Class<?> configClass) {
        checkConfigClass(configClass);

        discoveredProviders.addAll(discoverComponentProviders(configClass));
    }

    private void instantiateComponents() {
        for (ComponentProvider provider : discoveredProviders) {
            if (getAppComponent(provider.name()) == null) {
                createComponent(provider);
            }
        }
    }

    private Object createComponent(ComponentProvider provider) {
        try {
            var component = provider.method.invoke(provider.config, getDependencies(provider));
            appComponentsByName.put(provider.name(), component);
            return component;
        } catch (Exception e) {
            throw new ContainerException("Cannot create component " + provider.name(), e);
        }
    }

    private Object[] getDependencies(ComponentProvider provider) {
        var types = provider.method.getParameterTypes();
        var dependencies = new Object[types.length];
        if (types.length == 0) {
            return dependencies;
        }
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            var component = getAppComponent(type);
            if (component == null) {
                component = discoveredProviders.stream()
                        .filter(p -> p.getComponentType().equals(type))
                        .findFirst()
                        .map(this::createComponent)
                        .orElseThrow(() -> new ContainerException(
                                String.format("No provided component of %s type", type.getName())));
            }
            dependencies[i] = component;
        }
        return dependencies;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return appComponentsByName.values()
                .stream()
                .filter(componentClass::isInstance)
                .findFirst()
                .map(o -> (C) o)
                .orElse(null);
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }

    static Class<?>[] findConfigurations(String... scan) {
        var reflections = new Reflections(new ConfigurationBuilder().setUrls(packagesToUrls(scan))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(scan)));

        return reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class).toArray(Class[]::new);
    }

    static Collection<ComponentProvider> discoverComponentProviders(Class<?> config) {
        var reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forClass(config))
                .setScanners(new MethodAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().include(config.getName() + ".*")));

        var providers = reflections.getMethodsAnnotatedWith(AppComponent.class)
                .stream()
                .map(m -> new ComponentProvider(config, m.getDeclaredAnnotation(AppComponent.class), m))
                .collect(Collectors.toList());

        var configInstances = new HashMap<Class<?>, Object>();
        for (var provider : providers) {
            provider.setConfig(
                    configInstances.computeIfAbsent(provider.getConfigClass(), c -> provider.createConfigInstance()));
        }

        return providers;
    }

    private static Collection<URL> packagesToUrls(String[] pkgs) {
        return Arrays.stream(pkgs)
                .map(ClasspathHelper::forPackage)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Data
    static class ComponentProvider {
        private final Class<?> configClass;
        private Object config;
        @Delegate
        private final AppComponent properties;
        private final Method method;

        @SneakyThrows
        Object createConfigInstance() {
            return configClass.getDeclaredConstructor().newInstance();
        }

        public Class<?> getComponentType() {
            return method.getReturnType();
        }
    }
}
