package ru.otus.jdbc.sessionmanager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.core.sessionmanager.DatabaseSession;

import java.sql.Connection;

@RequiredArgsConstructor
@Getter
public class DatabaseSessionJdbc implements DatabaseSession {
    private final Connection connection;
}
