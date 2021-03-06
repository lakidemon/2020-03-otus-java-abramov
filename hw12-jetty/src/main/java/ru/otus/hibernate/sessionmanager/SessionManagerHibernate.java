package ru.otus.hibernate.sessionmanager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.core.sessionmanager.SessionManager;
import ru.otus.core.sessionmanager.SessionManagerException;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class SessionManagerHibernate implements SessionManager {
    @Getter
    private final EntityManagerFactory sessionFactory;
    private DatabaseSessionHibernate session;

    @Override
    public void beginSession() {
        if (session != null) {
            throw new SessionManagerException("DatabaseSession already begun");
        }
        session = new DatabaseSessionHibernate(sessionFactory.createEntityManager());
    }

    @Override
    public void commitSession() {
        checkSession();
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public void rollbackSession() {
        checkSession();
        try {
            session.getTransaction().rollback();
        } catch (Exception e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public void close() {
        checkSession();
        try {
            session.close();
            session = null;
        } catch (Exception e) {
            throw new SessionManagerException(e);
        }
    }

    @Override
    public DatabaseSessionHibernate getCurrentSession() {
        checkSession();
        return session;
    }

    private void checkSession() {
        if (session == null) {
            throw new SessionManagerException("DatabaseSession not begun");
        }
    }
}
