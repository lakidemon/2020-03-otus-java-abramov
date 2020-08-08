package ru.otus.messagesystem.client;

import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

import javax.annotation.Nullable;

public interface MsClient {

    boolean sendMessage(Message msg);

    void handle(Message msg);

    String getName();

    <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType, @Nullable MessageCallback<T> callback);
}
