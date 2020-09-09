package ru.otus.messagesystem.network.server;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.message.MessageHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Getter
@Slf4j
public class MessageSystemServerImpl implements MessageSystemServer {
    private final String host;
    private final int port;
    private final MessageSystem messageSystem;
    private boolean running = true;
    private Map<SocketAddress, RemoteMSClient> clients = new HashMap<java.net.SocketAddress, RemoteMSClient>();

    @Override
    @Async
    public void start() throws IOException {
        log.info("Starting at {}:{}", host, port);
        messageSystem.start();
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.configureBlocking(false);

            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(host, port));

            try (Selector selector = Selector.open()) {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                while (running) {
                    if (selector.select() > 0) {
                        performIO(selector);
                    }
                }
            }
        }
    }

    private void performIO(Selector selector) throws IOException {
        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            if (key.isAcceptable()) {
                acceptConnection(key, selector);
            } else if (key.isReadable()) {
                readPacket(key);
            }
            keys.remove();
        }
    }

    private void acceptConnection(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept(); //The socket channel for the new connection
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        clients.put(socketChannel.getRemoteAddress(), new RemoteMSClient(socketChannel, key));
    }

    private void readPacket(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        RemoteMSClient client = clients.get(socketChannel.getRemoteAddress());
        if (client.getName() == null) {
            client.setName(readName(socketChannel));
            messageSystem.addClient(client);
        } else {
            handleMessage(socketChannel);
        }
    }

    private String readName(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        StringBuilder inputBuffer = new StringBuilder(100);
        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            String input = StandardCharsets.UTF_8.decode(buffer).toString();

            buffer.flip();
            inputBuffer.append(input);
        }

        return inputBuffer.toString();
    }

    private void handleMessage(SocketChannel channel) {
        try {
            var sizeBuffer = ByteBuffer.allocate(4);
            channel.read(sizeBuffer);
            sizeBuffer.flip();
            var packetSize = sizeBuffer.getInt();
            var buffer = ByteBuffer.allocate(packetSize);
            channel.read(buffer);
            var rawMessage = buffer.array();
            var message = MessageHelper.deSerializeMessage(rawMessage);
            messageSystem.newMessage(message);
        } catch (IOException e) {
            log.warn("Error reading message {}: {}", e.getClass().getName(), e.getMessage());
            disconnectClient(channel);
        }
    }

    @SneakyThrows
    private void disconnectClient(SocketChannel channel) {
        var client = clients.remove(channel.getRemoteAddress());
        messageSystem.removeClient(client.getName());
        channel.close();
    }

    @Override
    public Collection<RemoteClient> getClients() {
        return ImmutableList.copyOf(clients.values());
    }

    @Override
    public Optional<RemoteClient> getClient(String name) {
        return clients.values()
                .stream()
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .findFirst()
                .map(RemoteClient.class::cast);
    }

    @Override
    public void stop() throws InterruptedException {
        log.info("Stopping server...");
        running = false;
        messageSystem.dispose();
    }
}
