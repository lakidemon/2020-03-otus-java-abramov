package ru.otus.web.auth;

public interface AuthService {

    boolean authenticate(String username, String password);

    void addUser(String name, String password);

}
