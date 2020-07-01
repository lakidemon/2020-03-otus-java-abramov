package ru.otus;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.model.Address;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;
import ru.otus.core.service.CachedDbServiceUser;
import ru.otus.core.service.DBServiceUser;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.hibernate.dao.UserDaoHibernate;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CacheTests {
    static Server dbServer;
    static HwListener<Long, User> cacheLogger = new HwListener<Long, User>() {
        @Override
        public void notify(@Nonnull Long key, @Nullable User value, String action) {
            log.info("{} user {} with id {}", action, value, key);
        }
    };
    static EntityManagerFactory factory;
    static SessionManagerHibernate sessionManager;
    static long testUserId;

    @SneakyThrows
    @BeforeAll
    static void setup() {
        dbServer = Server.createTcpServer("-ifNotExists", "-properties", "null", "-baseDir", "~").start();
        insertTestUser();
    }

    static void insertTestUser() {
        factory = Persistence.createEntityManagerFactory("otus-hibernate");
        sessionManager = new SessionManagerHibernate(factory);

        var user = new User("TestUser", 100, new Address("Vokzal"), Phone.single("88005553535"));
        new DbServiceUserImpl(new UserDaoHibernate(sessionManager)).saveUser(user);

        testUserId = user.getId();
        log.info("User id: {}", testUserId);
    }

    @AfterAll
    static void teardown() {
        dbServer.stop();
    }

    @Test
    @SneakyThrows
    void cachedServiceShouldRunFasterThanNonCached() {
        var nonCachedEnd = measureFetch(false);
        var cachedEnd = measureFetch(true);

        log.info("NON-cached time: {}ms.", nonCachedEnd.toMillis());
        log.info("cached time: {}ms.", cachedEnd.toMillis()); // typically 0
        log.info("delta: {}ms.", nonCachedEnd.minus(cachedEnd).abs().toMillis());
        assertTrue(cachedEnd.compareTo(nonCachedEnd) < 0, "Cached took longer");
    }

    @SneakyThrows
    Duration measureFetch(boolean cached) {
        var dao = new UserDaoHibernate(sessionManager);
        DBServiceUser service;
        if (cached) {
            var cache = new MyCache<Long, User>();
            cache.addListener(cacheLogger);
            service = new CachedDbServiceUser(dao, cache);
            service.getUser(1);
        } else {
            service = new DbServiceUserImpl(dao);
        }

        var start = Instant.now();
        var user = service.getUser(1);
        var time = Duration.between(start, Instant.now());
        assertTrue(user.isPresent(), "User not found");
        return time;
    }

}
