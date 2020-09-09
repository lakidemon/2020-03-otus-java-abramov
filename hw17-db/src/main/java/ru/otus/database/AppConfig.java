package ru.otus.database;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.otus.core.handlers.UserMessageType;
import ru.otus.database.handlers.AddUserRequestHandler;
import ru.otus.database.handlers.GetUserRequestHandler;
import ru.otus.database.handlers.GetUsersRequestHandler;
import ru.otus.database.service.DBServiceUser;
import ru.otus.database.service.DefaultUserPopulatorService;
import ru.otus.database.service.UserPopulatorService;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.network.client.NetworkClient;
import ru.otus.messagesystem.network.client.NetworkMSClient;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AppConfig implements AsyncConfigurer {
    @Value("${network-name:db}")
    private final String clientName;

    @Override
    @Bean
    public TaskExecutor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor("db-thread-");
    }

    @Bean(initMethod = "connect")
    NetworkClient networkClient(DBServiceUser dbServiceUser) {
        HandlersStore handlersStore = handlersStore();
        handlersStore.addHandler(UserMessageType.GET_ALL, new GetUsersRequestHandler(dbServiceUser));
        handlersStore.addHandler(UserMessageType.GET, new GetUserRequestHandler(dbServiceUser));
        handlersStore.addHandler(UserMessageType.ADD, new AddUserRequestHandler(dbServiceUser));
        return new NetworkMSClient(clientName, "127.0.0.1", 25555, callbackRegistry(), handlersStore,
                getAsyncExecutor());
    }

    @Bean
    EntityManagerFactory entityManager() {
        return Persistence.createEntityManagerFactory("otus-hibernate");
    }

    @Bean
    CallbackRegistry callbackRegistry() {
        return new CallbackRegistryImpl();
    }

    @Bean
    HandlersStore handlersStore() {
        return new HandlersStoreImpl();
    }

    @Bean(initMethod = "init")
    public UserPopulatorService populatorService(DBServiceUser serviceUser) {
        return new DefaultUserPopulatorService(serviceUser);
    }
}
