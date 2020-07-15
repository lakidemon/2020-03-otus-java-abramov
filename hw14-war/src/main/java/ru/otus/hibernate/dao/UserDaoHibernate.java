package ru.otus.hibernate.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.User;
import ru.otus.hibernate.sessionmanager.SessionManagerHibernate;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
public class UserDaoHibernate implements UserDao {
    @Getter
    private final SessionManagerHibernate sessionManager;

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(getSession().find(User.class, id));
    }

    @Override
    public long insertUser(User user) {
        getSession().persist(user);
        return user.getId();
    }

    @Override
    public void updateUser(User user) {
        getSession().merge(user);
    }

    @Override
    public void insertOrUpdate(User user) {
        if (user.getId() > 0) {
            getSession().merge(user);
        } else {
            getSession().persist(user);
        }
    }

    @Override
    public Collection<User> findAll() {
        return getSession().createQuery("SELECT u FROM User u").getResultList();
    }

    @Override
    public Optional<User> findAny() {
        return Optional.ofNullable((User) getSession().createQuery("SELECT u FROM User u ORDER BY rand()")
                .setMaxResults(1)
                .getSingleResult());
    }

    private EntityManager getSession() {
        return sessionManager.getCurrentSession().getSession();
    }
}
