package ru.otus.messagesystem.network.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class NetworkMSClient implements NetworkClient {
    @Getter
    private final String name;
    private final String host;
    private final int port;
    private final CallbackRegistry callbackRegistry;
    @Getter
    private final HandlersStore handlersStore;
    private final TaskExecutor executor;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    @Override
    @Async
    @SneakyThrows
    public CompletableFuture<Void> connect() {
        socket = new Socket();
        log.info("Connecting to {}:{}. Name: {}", host, port, name);
        socket.connect(new InetSocketAddress(host, port));
        log.info("Connected... Initializing");
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        log.info("Sending name...");
        sendName();
        executor.execute(this::startReading);
        log.info("Connected");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            log.info("Closing socket...");
            socket.close();
        } else
            log.warn("Called close() when socket is already closed.");
    }

    @Override
    public boolean sendMessage(Message msg) {
        try {
            byte[] bytes = MessageHelper.serializeMessage(msg);
            outputStream.writeInt(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            log.error("", e);
            return false;
        }
    }

    private void sendName() throws IOException {
        outputStream.write(name.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    void startReading() {
        while (socket.isConnected()) {
            try {
                var packetSize = inputStream.readInt();
                var buffer = new byte[packetSize];
                inputStream.readFully(buffer);
                var message = MessageHelper.deSerializeMessage(buffer);
                handle(message);
            } catch (IOException e) {
                log.warn("Got exception {}: {}", e.getClass().getName(), e.getMessage());
                return;
            }
        }
    }

    @Override
    public void handle(Message msg) {
        log.info("new message:{}", msg);
        try {
            RequestHandler requestHandler = handlersStore.getHandlerByType(msg.getType());
            if (requestHandler != null) {
                requestHandler.handle(msg).ifPresent(message -> sendMessage((Message) message));
            } else {
                log.error("handler not found for the message type:{}", msg.getType());
            }
        } catch (Exception ex) {
            log.error("msg:{}", msg, ex);
        }
    }

    @Override
    public <T extends ResultDataType> Message produceMessage(String to, T data, MessageType msgType,
            MessageCallback<T> callback) {
        Message message = MessageBuilder.buildMessage(name, to, null, data, msgType);
        if (callback != null) {
            callbackRegistry.put(message.getCallbackId(), callback);
        }
        return message;
    }

}
