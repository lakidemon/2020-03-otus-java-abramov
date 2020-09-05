package ru.otus.frontend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.otus.core.dto.UserDto;
import ru.otus.core.dto.UserListDto;
import ru.otus.core.handlers.UserMessageType;
import ru.otus.messagesystem.network.client.NetworkClient;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserWebSocketController {
    private final NetworkClient client;
    private final SimpMessagingTemplate simp;
    @Value("${db-client-name:db}")
    private String dbClientName;

    @MessageMapping("/get")
    public void getAllUsers(SimpMessageHeaderAccessor headers) {
        var remoteClient = headers.getUser().getName();
        var msg = client.produceMessage(dbClientName, null, UserMessageType.GET_ALL,
                (UserListDto data) -> simp.convertAndSendToUser(remoteClient, "/topic/userlist", data.getUsers()));
        client.sendMessage(msg);
    }

    @MessageMapping("/get.{id}")
    public void getUser(@DestinationVariable int id, SimpMessageHeaderAccessor headers) {
        requestUser(headers.getUser().getName(), id);
    }

    @MessageMapping("/get.random")
    public void getRandomUser(SimpMessageHeaderAccessor headers) {
        requestUser(headers.getUser().getName(), -1);
    }

    private void requestUser(String by, long id) {
        var msg = client.produceMessage(dbClientName, UserDto.id(id), UserMessageType.GET,
                (UserDto data) -> simp.convertAndSendToUser(by, "/topic/requesteduser", data));
        client.sendMessage(msg);
    }

    @MessageMapping("/add")
    public void addUser(@Payload UserDto user) {
        var msg = client.produceMessage(dbClientName, user, UserMessageType.ADD,
                dto -> simp.convertAndSend("/topic/newuser", dto));
        client.sendMessage(msg);
    }
}
