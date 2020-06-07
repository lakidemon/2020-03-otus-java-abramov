package ru.otus;

import lombok.extern.slf4j.Slf4j;
import ru.otus.core.model.User;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.dao.UserDaoJdbc;
import ru.otus.jdbc.sessionmanager.SessionManagerJdbc;

/**
 * @author sergey
 * created on 03.02.19.
 */
@Slf4j
public class DbServiceDemo {

    public static void main(String[] args) throws Exception {
        var dataSource = new DataSourceH2(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE;DATABASE_TO_UPPER=FALSE;INIT=runscript from "
                        + "'classpath:init.sql'");

        var sessionManager = new SessionManagerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl<User>();
        var userDao = new UserDaoJdbc(sessionManager, dbExecutor);

        var dbServiceUser = new DbServiceUserImpl(userDao);
        var id = dbServiceUser.saveUser(new User(0, "dbServiceUser", 100));
        var user = dbServiceUser.getUser(id);

        user.ifPresentOrElse(crUser -> log.info("created user, name:{}", crUser.getName()),
                () -> log.info("user was not created"));
    }

}
