package ru.otus.messagesystem.network.server;

import java.nio.channels.SelectionKey;

public interface RemoteClient {

    SelectionKey getSelectionKey();

    void setName(String name);

    String getName();

}
