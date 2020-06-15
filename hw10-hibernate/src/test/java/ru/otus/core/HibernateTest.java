package ru.otus.core;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.core.model.Address;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class HibernateTest {
    EntityManagerFactory factory;
    UserDaoHibernate dao;
    DBServiceUser service;

    @BeforeEach
    void setUp() {
        factory = Persistence.createEntityManagerFactory("otus-hibernate");
        dao = new UserDaoHibernate(new SessionManagerHibernate(factory));
        service = new DbServiceUserImpl(dao);
    }

    @AfterEach
    void tearDown() {
        factory.close();
    }

    @Test
    @DisplayName("должен сохранять пользователя корректно(без update-ов)")
    void shouldSaveUserCorrectly() {
        var user = testUser();
        assertNotEquals(0, service.saveUser(user));
        assertEquals(3, stats().getEntityInsertCount());
        assertEquals(0, stats().getEntityUpdateCount());
    }

    @Test
    @DisplayName("должен находить сохранённого пользователя")
    void shouldFindSavedUser() {
        var testUser = testUser();
        var id = service.saveUser(testUser);

        var user = service.getUser(id);
        assertTrue(user.isPresent());
        assertEquals(testUser, user.get());
    }

    @Test
    @DisplayName("должен успешно обновлять пользователя")
    void shouldUpdateUserPhonesCorrectly() {
        var stats = stats();
        var testUser = testUser();
        var id = service.saveUser(testUser);
        stats.clear();

        testUser.getPhones().remove(0);
        testUser.getPhones().add(new Phone("88001000000"));
        testUser.getPhones().add(new Phone("+79130000000"));
        testUser.setAddress(new Address("Russian Federation"));
        service.updateUser(testUser);

        var user = service.getUser(id);
        assertTrue(user.isPresent());
        assertEquals(2, user.get().getPhones().size());
        assertEquals("Russian Federation", user.get().getAddress().getStreet());

        assertEquals(1, stats.getEntityDeleteCount());
        assertEquals(3, stats.getEntityInsertCount());
        assertEquals(1, stats.getEntityUpdateCount());
    }

    @Test
    @DisplayName("должен создавать только 3 таблицы")
    void shouldCreateOnlyThreeTables() {
        var count = (Number) factory.createEntityManager()
                .createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='TABLE'")
                .getSingleResult();
        assertEquals(3, count.intValue());
    }

    User testUser() {
        return new User("Gems", 49, new Address("Soviet Union"), Phone.single("88005553535"));
    }

    Statistics stats() {
        return factory.unwrap(SessionFactory.class).getStatistics();
    }
}