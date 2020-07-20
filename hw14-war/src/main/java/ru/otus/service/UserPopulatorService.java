package ru.otus.service;

import org.springframework.beans.factory.InitializingBean;

public interface UserPopulatorService extends InitializingBean {

    void populate(DBServiceUser userService);

}
