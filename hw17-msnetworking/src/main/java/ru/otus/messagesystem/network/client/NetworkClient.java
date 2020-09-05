package ru.otus.messagesystem.network.client;

import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface NetworkClient extends Closeable, MsClient {

    String getName();

    CompletableFuture<Void> connect() throws IOException;

    void close() throws IOException;

    boolean sendMessage(Message message);

    <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType,
            MessageCallback<T> callback);
}
