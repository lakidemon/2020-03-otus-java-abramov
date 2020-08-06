package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserWebSocketController {
    private final SimpMessagingTemplate template;

    @MessageMapping("/hello")
    public void helloWorld(String message, SimpMessageHeaderAccessor headers) {
        System.out.println("Got it: " + message + " from " + headers.getUser().getName());
        template.convertAndSend("/topic/test", "Response");
    }

}
