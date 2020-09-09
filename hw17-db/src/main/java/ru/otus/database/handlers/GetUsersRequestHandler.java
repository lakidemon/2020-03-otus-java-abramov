package ru.otus.database.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.core.dto.UserListDto;
import ru.otus.core.model.User;
import ru.otus.database.service.DBServiceUser;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class GetUsersRequestHandler implements RequestHandler<UserListDto> {
    private final DBServiceUser dbServiceUser;

    @Override
    public Optional<Message> handle(Message msg) {
        var dto = new UserListDto(dbServiceUser.getAll().stream().map(User::toDto).collect(Collectors.toList()));
        return Optional.of(MessageBuilder.buildReplyMessage(msg, dto));
    }

}
