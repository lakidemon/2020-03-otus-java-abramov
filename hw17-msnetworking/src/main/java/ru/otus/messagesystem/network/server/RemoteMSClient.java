package ru.otus.messagesystem.network.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
@Getter
public class RemoteMSClient implements RemoteClient, MsClient {
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    @Setter
    private String name;

    @Override
    public boolean sendMessage(Message msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows
    public void handle(Message msg) {
        byte[] bytes = MessageHelper.serializeMessage(msg);
        var buffer = ByteBuffer.allocate(bytes.length + 4);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        socketChannel.write(buffer);
    }

    @Override
    public <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType,
            MessageCallback<T> callback) {
        return null;
    }

}
