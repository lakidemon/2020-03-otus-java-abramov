package ru.otus.core.service;

import lombok.Getter;
import ru.otus.cachehw.HwCache;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.User;

import java.util.Optional;

@Getter
public class CachedDbServiceUser extends DbServiceUserImpl {
    private HwCache<Long, User> userCache;

    public CachedDbServiceUser(UserDao userDao, HwCache<Long, User> cache) {
        super(userDao);
        this.userCache = cache;
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(userCache.get(id)).or(() -> {
            var fetched = super.getUser(id);
            fetched.ifPresent(user -> userCache.put(id, user));
            return fetched;
        });
    }

    @Override
    public long saveUser(User user) {
        var id = super.saveUser(user);
        userCache.put(id, user);
        return id;
    }

}
