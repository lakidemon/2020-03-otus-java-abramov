package ru.otus.messageserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageSystemImpl;
import ru.otus.messagesystem.network.server.MessageSystemServer;
import ru.otus.messagesystem.network.server.MessageSystemServerImpl;

@Configuration
@EnableAsync
public class ServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageSystemServer messageSystemServer() {
        return notStartedServer();
    }

    @Bean(autowireCandidate = false)
    public MessageSystemServer notStartedServer() {
        return new MessageSystemServerImpl("127.0.0.1", 25555, messageSystem());
    }

    @Bean
    public MessageSystem messageSystem() {
        return new MessageSystemImpl(false);
    }

}
