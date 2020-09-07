package ru.otus.frontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.otus.core.handlers.UserMessageType;
import ru.otus.frontend.handlers.UsersResponseHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.network.client.NetworkClient;
import ru.otus.messagesystem.network.client.NetworkMSClient;

import java.util.Arrays;

@Configuration
@EnableAsync
@ComponentScan("ru.otus.frontend")
@RequiredArgsConstructor
public class AppConfig implements AsyncConfigurer {
    @Value("${network-name:front}")
    private final String clientName;

    @Override
    @Bean
    public TaskExecutor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor("front-thread-");
    }

    @Bean(initMethod = "connect")
    NetworkClient networkClient() {
        HandlersStore handlersStore = handlersStore();
        var responseHandler = new UsersResponseHandler(callbackRegistry());
        Arrays.stream(UserMessageType.values()).forEach(type -> handlersStore.addHandler(type, responseHandler));
        return new NetworkMSClient(clientName, "127.0.0.1", 25555, callbackRegistry(), handlersStore,
                getAsyncExecutor());
    }

    @Bean
    CallbackRegistry callbackRegistry() {
        return new CallbackRegistryImpl();
    }

    @Bean
    HandlersStore handlersStore() {
        return new HandlersStoreImpl();
    }

}