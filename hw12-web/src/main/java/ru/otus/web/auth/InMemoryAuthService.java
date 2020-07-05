package ru.otus.web.auth;

import javax.inject.Singleton;

@Singleton
public class InMemoryAuthService implements AuthService {
    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public void addUser(String name, String password) {

    }
}
