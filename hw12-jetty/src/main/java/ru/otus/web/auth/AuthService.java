package ru.otus.web.auth;

import org.eclipse.jetty.security.AbstractLoginService;

import java.util.Optional;

public interface AuthService {

    Optional<AbstractLoginService.UserPrincipal> authenticate(String username, String password);

    AbstractLoginService.UserPrincipal addUser(String name, String password);

}
