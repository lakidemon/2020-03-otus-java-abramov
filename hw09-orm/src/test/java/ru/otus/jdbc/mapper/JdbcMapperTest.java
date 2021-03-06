package ru.otus.jdbc.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.core.model.User;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutor;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.sessionmanager.SessionManagerJdbc;
import ru.otus.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Самодельный маппер")
class JdbcMapperTest {
    SessionManager sessionManager;
    DbExecutor executor;

    @Test
    @DisplayName("должен успешно создаваться")
    void shouldCreateSuccessfully() {
        assertDoesNotThrow(() -> create(User.class));
    }

    @Test
    @DisplayName("должен вставлять пользователя")
    void shouldInsertUser() {
        var mapper = create(User.class);
        var newUser = new User(0, "TestUser", 100);
        mapper.insert(newUser);
        assertEquals(100, newUser.getId()); // в таблице находится 99 строк
    }

    @Test
    @DisplayName("должен находить по id")
    void shouldFindUsersCorrectly() {
        var mapper = create(User.class);
        var userOpt = mapper.findById(42);

        assertTrue(userOpt.isPresent(), "User not found");
        assertEquals("Kabul", userOpt.get().getName());

        var notUserOpt = mapper.findById(228);
        assertFalse(notUserOpt.isPresent(), () -> "Found user with invalid id: " + notUserOpt.get());
    }

    @Test
    @DisplayName("должен поддерживать Account из дз")
    void shouldSupportAccount() {
        var mapper = create(Account.class);
        mapper.insert(new Account("TestAcc", BigDecimal.TEN));

        var inserted = mapper.findById(1);
        assertTrue(inserted.isPresent(), "Account not found");
        assertEquals("TestAcc", inserted.get().getType());
    }

    @Test
    @DisplayName("должен успешно обновлять существующие записи")
    void shouldUpdateCorrectly() {
        var mapper = create(User.class);
        var kabul = mapper.findById(42).get();
        kabul.setName("Barnaul");
        mapper.update(kabul);

        kabul = mapper.findById(42).get();
        assertEquals("Barnaul", kabul.getName());

        assertThrows(MapperException.class, () -> mapper.update(new User(0, "NotExists", 1)));
    }

    @Test
    @DisplayName("должен вставлять несуществующие записи и обновлять существующие")
    void shouldInsertOrUpdateCorrectly() {
        var mapper = create(Account.class);
        var newAccount = new Account("SuperAcc", BigDecimal.TEN);
        mapper.insertOrUpdate(newAccount);
        assertEquals(1, newAccount.getNo());

        newAccount.setType("CommonAcc");
        mapper.insertOrUpdate(newAccount);
        assertEquals(1, newAccount.getNo());
        assertEquals("CommonAcc", mapper.findById(1).get().getType());
    }

    @BeforeEach
    void setup() {
        var h2 = new DataSourceH2(
                "jdbc:h2:mem:test;IGNORECASE=TRUE;DATABASE_TO_UPPER=FALSE;INIT=runscript from 'classpath:test.sql'");
        sessionManager = new SessionManagerJdbc(h2);
        executor = new DbExecutorImpl();
        sessionManager.beginSession();
    }

    @AfterEach
    void tearDown() {
        sessionManager.close();
    }

    <T> JdbcMapperImpl<T> create(Class<T> model) {
        return JdbcMapperImpl.forType(model, sessionManager, executor);
    }
}
