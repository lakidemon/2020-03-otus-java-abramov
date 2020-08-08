package ru.otus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.otus.UserMessageType;
import ru.otus.handlers.AddUserRequestHandler;
import ru.otus.handlers.GetUserRequestHandler;
import ru.otus.handlers.GetUsersRequestHandler;
import ru.otus.handlers.UsersResponseHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageSystemImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    public static final String FRONTEND_CLIENTNAME = "frontend";
    public static final String DB_CLIENTNAME = "db";

    private final ApplicationContext context;

    @Bean
    EntityManagerFactory entityManager() {
        return Persistence.createEntityManagerFactory("otus-hibernate");
    }

    @Bean
    MessageSystem messageSystem() {
        return new MessageSystemImpl(true);
    }

    @Bean
    MsClient frontendClient(MessageSystem messageSystem, HandlersStore handlersStore,
            CallbackRegistry callbackRegistry) {
        var client = new MsClientImpl(FRONTEND_CLIENTNAME, messageSystem, handlersStore, callbackRegistry);
        var responseHandler = context.getBean(UsersResponseHandler.class);
        Arrays.stream(UserMessageType.values()).forEach(type -> handlersStore.addHandler(type, responseHandler));
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    MsClient dbClient(MessageSystem messageSystem, HandlersStore handlersStore, CallbackRegistry callbackRegistry) {
        var client = new MsClientImpl(DB_CLIENTNAME, messageSystem, handlersStore, callbackRegistry);
        handlersStore.addHandler(UserMessageType.GET_ALL, context.getBean(GetUsersRequestHandler.class));
        handlersStore.addHandler(UserMessageType.GET, context.getBean(GetUserRequestHandler.class));
        handlersStore.addHandler(UserMessageType.ADD, context.getBean(AddUserRequestHandler.class));
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    @Scope("prototype")
    HandlersStore handlersStore() {
        return new HandlersStoreImpl();
    }
}
