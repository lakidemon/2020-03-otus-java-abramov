package ru.otus.messagesystem.network.server;

import ru.otus.messagesystem.MessageSystem;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface MessageSystemServer {

    void start() throws IOException;

    void stop() throws InterruptedException;

    MessageSystem getMessageSystem();

    Collection<RemoteClient> getClients();

    Optional<RemoteClient> getClient(String name);

}
